package mmud.database.entities.paypal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "paypalpayment")
@SequenceGenerator(name = "seq_paypalpayment", sequenceName = "seq_paypalpayment", allocationSize = 1)
public class PaypalPayment {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_paypalpayment")
  @Column(name = "id", unique = true, nullable = false, precision = 10, scale = 0)
  @NotNull
  private Long id;

  @Column
  private String paymentDate;

  @Column
  private String mcCurrency;

  @Column
  private String paymentType;

  @Column
  private String payerEmail;

  @Column
  private String txnId;

  @Column
  private String payerId;

  @Column
  private String memo;

  @Column
  private BigDecimal mcGross;

  @Column
  private BigDecimal mcFee;

  @Column
  private LocalDateTime creation;

  public PaypalPayment() {
    creation = LocalDateTime.now();
  }

  public PaypalPayment(String paymentDate, String mcCurrency, String paymentType, String payerEmail,
      String txnId, String payerId, String memo, BigDecimal mcGross, BigDecimal mcFee) {
    this();
    this.paymentDate = paymentDate;
    this.mcCurrency = mcCurrency;
    this.paymentType = paymentType;
    this.payerEmail = payerEmail;
    this.txnId = txnId;
    this.payerId = payerId;
    this.memo = memo;
    this.mcGross = mcGross;
    this.mcFee = mcFee;
  }

}
