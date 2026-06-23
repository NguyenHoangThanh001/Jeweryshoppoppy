package online.jewerystorepoppy.be.model;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class TransactionPageRequestDTO extends PageRequestDTO{
    public double TotalMoneyFrom = 0;
    public double TotalMoneyTo = Double.MAX_VALUE;
    public LocalDateTime DateFrom;
    public LocalDateTime DateTo;
    public boolean sortByUsernameAscending = true;
}
