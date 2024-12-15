package com.boggle_boggle.bbegok.entity.user;

import com.boggle_boggle.bbegok.enums.FontType;
import com.boggle_boggle.bbegok.enums.SortingType;
import com.boggle_boggle.bbegok.enums.ThemeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class UserSettings {
    @Id
    @Column(name = "user_settings_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userSettingsSeq;

    @OneToOne
    @JoinColumn(name = "user_seq")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "sorting_type", nullable = false)
    private SortingType sortingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "font_type", nullable = false)
    private FontType fontType;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme_type", nullable = false)
    private ThemeType themeType;

    @PrePersist
    public void setDefaultUserSettings() {
        if (sortingType == null) sortingType = SortingType.newest_first;
        if (fontType == null) fontType = FontType.basic;
        if (themeType == null) themeType = ThemeType.basic;
    }

    protected UserSettings(){}

    protected UserSettings(User user){
        this.user = user;
    }

    public static UserSettings createUserSettings(User user) {
        return new UserSettings(user);
    }

    public void updateSortingType(SortingType sortingType) {
        this.sortingType = sortingType;
    }
}
