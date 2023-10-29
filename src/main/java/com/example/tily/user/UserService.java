package com.example.tily.user;

import com.example.tily._core.errors.exception.ExceptionCode;
import com.example.tily._core.errors.exception.CustomException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.tily._core.errors.exception.Exception400;
import com.example.tily._core.errors.exception.Exception403;
import com.example.tily._core.errors.exception.Exception404;
import com.example.tily._core.errors.exception.Exception500;
import com.example.tily._core.security.JWTProvider;
import com.example.tily._core.utils.RedisUtils;
import com.example.tily.til.Til;
import com.example.tily.til.TilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.desktop.SystemEventListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final RedisUtils redisUtils;
    private final TilRepository tilRepository;

    // (회원가입) 이메일 중복 체크 후 인증코드 전송
    @Transactional
    public void checkEmail(UserRequest.CheckEmailDTO requestDTO) {
        Optional<User> user = userRepository.findByEmail(requestDTO.getEmail());
        if (user.isPresent()) throw new CustomException(ExceptionCode.USER_EMAIL_EXIST);

        sendCode(requestDTO.getEmail());
    }

    // 인증코드 전송
    @Transactional
    public void sendEmailCode(UserRequest.SendEmailCodeDTO requestDTO) {

        User user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_EMAIL_NOT_FOUND));

        sendCode(requestDTO.getEmail());
    }

    // 인증코드 확인
    @Transactional
    public UserResponse.CheckEmailCodeDTO checkEmailCode(UserRequest.CheckEmailCodeDTO requestDTO) {
        String code = redisUtils.getData(requestDTO.getEmail()); // 이메일로 찾은 코드

        if (code==null) {
            throw new CustomException(ExceptionCode.CODE_EXPIRED);
        }

        if (!code.equals(requestDTO.getCode())) {
            throw new CustomException(ExceptionCode.CODE_WRONG);
        }

        redisUtils.deleteData(requestDTO.getEmail()); // 인증 완료 후 인증코드 삭제

        return new UserResponse.CheckEmailCodeDTO(requestDTO.getEmail());
    }

    @Transactional
    public void join(UserRequest.JoinDTO requestDTO) {
        Optional<User> user = userRepository.findByEmail(requestDTO.getEmail());
        if (user.isPresent()) {
            throw new CustomException(ExceptionCode.USER_EMAIL_EXIST);
        }
        requestDTO.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        userRepository.save(requestDTO.toEntity());
    }

    @Transactional
    public UserResponse.TokenDTO login(UserRequest.LoginDTO requestDTO) {
        User user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_EMAIL_NOT_FOUND));

        if(!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            throw new CustomException(ExceptionCode.USER_PASSWORD_WRONG);
        }

        return createToken(user);
    }

    @Transactional
    public UserResponse.TokenDTO refresh(String refreshToken) {
        DecodedJWT decodedJWT = JWTProvider.verify(refreshToken);
        Long userId = decodedJWT.getClaim("id").asLong();
        if (!redisUtils.existData(userId.toString())) {
            throw new Exception403("Refresh 토큰이 만료됐습니다.");
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new Exception404("해당 사용자를 찾을 수 없습니다.")
        );

        return createToken(user);
    }

    @Transactional
    public void changePassword(UserRequest.ChangePwdDTO requestDTO) {
        User user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_EMAIL_NOT_FOUND));

        String enPassword = passwordEncoder.encode(requestDTO.getPassword());
        user.updatePassword(enPassword);
    }

    // 해당 이메일로 인증코드 전송
    public void sendCode(String email) {
        try {
            if (redisUtils.existData(email)) { // 이메일에 대한 인증코드 존재하면 예전 인증코드 삭제
                redisUtils.deleteData(email);
            }
            // 랜덤한 인증코드 생성
            String code = createCode();

            // 이메일로 인증코드 전송
            MimeMessage message = javaMailSender.createMimeMessage();
            message.addRecipients(Message.RecipientType.TO, email);
            message.setSubject("[TIL-y] 인증코드가 도착했습니다.");
            message.setText(getEmailContent(code), "utf-8", "html");
            javaMailSender.send(message);

            // redis에 인증코드 저장 (유효기간: 5분)
            redisUtils.setDataExpire(email, code, 60 * 5L);
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.CODE_NOT_SEND);
        }
    }

    @Transactional
    public void updateUser(UserRequest.UpdateUserDTO requestDTO, User user) {
        User user1 = userRepository.findById(user.getId())
                .orElseThrow(()->new CustomException(ExceptionCode.USER_NOT_FOUND));

        if (!user1.equals(user))
            throw new CustomException(ExceptionCode.USER_UPDATE_FORBIDDEN);

        if (!user1.getPassword().equals(requestDTO.getCurPassword()))
            throw new CustomException(ExceptionCode.USER_CURPASSWORD_WRONG);

        if (!requestDTO.getNewPassword().equals(requestDTO.getNewPassword()))
            throw new CustomException(ExceptionCode.USER_PASSWORD_WRONG);

        user1.updatePassword(requestDTO.getNewPassword());
    }

    // 인증코드 랜덤 생성
    public String createCode() {
        int start = 48;
        int end = 90;
        int codeLength = 8;

        Random random = new Random();

        return random.ints(start, end + 1).filter(i -> (i <= 57 || i >= 65)).limit(codeLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }

    public String getEmailContent(String code) {

        String content = "<div style='background-color: #f8f9fa; padding: 10px 20px 50px 20px; margin: 20px'>" +
                "<h1 style='margin: 20px'>이메일 인증</h1>" +
                "<p>안녕하세요!<br>본인 인증을 위해 아래의 코드를 복사해 인증코드 입력 칸에 입력해주세요.<br>" +
                "<div style='background-color:gainsboro; font-size: 30px; margin: 10px; padding: 5px; width: 250px' >" + code + "</div><br>" +
                "TIL-y 서비스를 이용해주셔서 감사합니다. <br></p></div>";
        return content;
    }

    public UserResponse.TokenDTO createToken(User user) {

        String newRefreshToken = JWTProvider.createRefreshToken(user);
        String newAccessToken = JWTProvider.createAccessToken(user);
        redisUtils.deleteData(user.getId().toString());
        redisUtils.setDataExpire(user.getId().toString(), newRefreshToken, JWTProvider.REFRESH_EXP);

        return new UserResponse.TokenDTO(newAccessToken, newRefreshToken);
    }
  
    public UserResponse.ViewGardensDTO viewGardens(User user) {
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime beginDateTime = endDateTime.minusYears(1).plusDays(1);

        Long userId = user.getId();
        HashMap<String, Integer> maps = new HashMap<>();

        List<Til> tils = tilRepository.findTilsByUserIdAndDateRange(userId, beginDateTime, endDateTime);

        // 모든 날에 대한 작성여부 0으로 초기화.
        for (LocalDate date = beginDateTime.toLocalDate(); !date.isAfter(endDateTime.toLocalDate()); date = date.plusDays(1)) {
            maps.put(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), 0);
        }

        // TIL 존재하는 날의 작성여부만 1로 변경.
        for (Til til : tils) {
            LocalDate tilDate = til.getCreatedDate().toLocalDate();
            maps.put(tilDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), 1);
        }

        return new UserResponse.ViewGardensDTO(maps);
    }

}
