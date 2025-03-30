package mmud.database.entities.paypal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "paypallog")
@SequenceGenerator(name = "seq_paypallog", sequenceName = "seq_paypallog", allocationSize = 1)
public class PaypalLog {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_paypallog")
  @Column(name = "id", unique = true, nullable = false, precision = 10, scale = 0)
  @NotNull
  private Long id;

  @Column
  private String message;

  @Column
  private String txnId;

  @Column
  private String stacktrace;

  @Column
  private LocalDateTime creation;

  public PaypalLog() {
    creation = LocalDateTime.now();
  }

  public PaypalLog(String message, String txnId, String stacktrace) {
    this();
    this.message = message;
    this.txnId = txnId;
    this.stacktrace = stacktrace;
  }
}
