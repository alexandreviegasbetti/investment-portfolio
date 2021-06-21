package br.com.alexandre.investmentportfolio.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Investment {

    private String id;
    private String name;
    private String code;
    private Integer quantity;
    private Double value;
    private Double totalValue;
    private String purchaseDate;

}
