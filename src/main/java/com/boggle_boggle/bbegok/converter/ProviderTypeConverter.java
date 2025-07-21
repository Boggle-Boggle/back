package com.boggle_boggle.bbegok.converter;

import com.boggle_boggle.bbegok.oauth.entity.ProviderType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/** oauth 인증서버에서의 콜백url의 소문자를 대문자 기반 enum Type으로 변경
 */
@Component
public class ProviderTypeConverter implements Converter<String, ProviderType> {
    @Override
    public ProviderType convert(String source) {
        return ProviderType.from(source);
    }
}
