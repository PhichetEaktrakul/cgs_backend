package com.base.encode.model.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoldPriceLog {
    private Long id;

    private Double gold99_buy;

    private Double gold99_sell;

    private Double old_gold99_buy;

    private Double old_gold99_sell;

    private Double gold96_buy;

    private Double gold96_sell;

    private Double old_gold96_buy;

    private Double old_gold96_sell;

    private String created_at;
}
