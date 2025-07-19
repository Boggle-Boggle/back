package com.boggle_boggle.bbegok.testfactory;


import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.oauth.entity.ProviderType;
import com.boggle_boggle.bbegok.oauth.entity.RoleType;
import org.springframework.test.util.ReflectionTestUtils;

public class UserTestFactory {

    public static User createDefault() {
        return create(1L);
    }

    public static User create(Long userSeq) {
        User user = User.createUser("testUser", ProviderType.GOOGLE,"test@example.com", RoleType.USER);
        ReflectionTestUtils.setField(user, "userSeq", userSeq);
        return user;
    }
}
