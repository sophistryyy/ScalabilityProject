package seng468scalability.com.portfolio.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PortfolioEntryId implements Serializable {
    private Long stockId;
    private String username;

}
