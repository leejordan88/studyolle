package com.studyolle.settings;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    public void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("jordan")
    @DisplayName("프로필 수정 폼")
    public void updateProfileForm() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @Test
    @WithAccount("jordan")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    public void updateProfile_success() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
            .param("bio", bio)
            .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account jordan = accountRepository.findByNickname("jordan");
        assertEquals(bio, jordan.getBio());
    }

    @Test
    @WithAccount("jordan")
    @DisplayName("프로필 수정하기 - 입력값 에러")
    public void updateProfile_error() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.짧은 소개를 수정하는 경우.짧은 소개를 수정하는 경우.짧은 소개를 수정하는 경우.짧은 소개를 수정하는 경우.짧은 소개를 수정하는 경우.";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account jordan = accountRepository.findByNickname("jordan");
        assertNull(jordan.getBio());
    }

    @Test
    @DisplayName("패스워드 수정 폼")
    @WithAccount("jordan")
    public void updatePassword_form() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"))
                ;
    }

    @Test
    @WithAccount("jordan")
    @DisplayName("패스워드 수정하기 - 입력값 정상")
    public void updatePassword_success() throws Exception {
        String password = "12345678";
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                .param("newPassword", password)
                .param("newPasswordConfirm", password)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account jordan = accountRepository.findByNickname("jordan");
        assertTrue(passwordEncoder.matches(password, jordan.getPassword()));
    }

    @Test
    @WithAccount("jordan")
    @DisplayName("패스워드 수정하기 - 입력값 에러")
    public void updatePassword_error() throws Exception {
        String password = "12345678";
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                .param("newPassword", password)
                .param("newPasswordConfirm", "password")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().hasErrors());
    }

}