package com.base.encode.model.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoldAssnResponse {
    private Long id;

    private String sellPrice;

    private String buyPrice;

    private String updatedTime;

    private String createdAt;
}
