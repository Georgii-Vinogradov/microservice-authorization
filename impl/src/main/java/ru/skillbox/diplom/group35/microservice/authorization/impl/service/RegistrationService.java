package ru.skillbox.diplom.group35.microservice.authorization.impl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skillbox.diplom.group35.library.core.security.config.TechnicalUserConfig;
import ru.skillbox.diplom.group35.microservice.account.api.client.AccountFeignClient;
import ru.skillbox.diplom.group35.microservice.account.api.dto.AccountDto;
import ru.skillbox.diplom.group35.microservice.authorization.api.dto.RegistrationDto;
import ru.skillbox.diplom.group35.microservice.authorization.impl.mapper.RegistrationMapper;

import java.time.ZonedDateTime;

/**
 * RegistrationService
 *
 * @author Mikhail Chechetkin
 */

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationMapper registrationMapper;
    private final AccountFeignClient accountFeignClient;
    private final CaptchaService captchaService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TechnicalUserConfig technicalUserConfig;

    public void create(RegistrationDto dto) {
        AccountDto accountDto = registrationMapper.mapToAccount(dto);
        accountDto.setIsDeleted(false);
        accountDto.setCreatedOn(ZonedDateTime.now());
        accountDto.setUpdatedOn(ZonedDateTime.now());

        if(dto.getPassword1().equals(dto.getPassword2())
        && captchaService.captchaValidation(dto)
        ){
            accountDto.setPassword(bCryptPasswordEncoder.encode(accountDto.getPassword()));
            technicalUserConfig.executeByTechnicalUser(()-> accountFeignClient.create(accountDto));
        }
    }
}
