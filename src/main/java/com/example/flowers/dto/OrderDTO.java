package com.example.flowers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    @JsonProperty("status")
    private String status;

    // Принимаем список идентификаторов продуктов (могут быть дубли)
    @JsonProperty("product_ids")
    private List<Long> productIds;

    @JsonProperty("user_id")
    private Long userId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
