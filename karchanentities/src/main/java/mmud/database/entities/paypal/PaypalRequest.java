package mmud.database.entities.paypal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

@Entity
@Table(name = "paypalrequests")
@SequenceGenerator(name = "seq_paypalrequests", sequenceName = "seq_paypalrequests", allocationSize = 1)
public class PaypalRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_paypalrequests")
  @Column(name = "id", unique = true, nullable = false, precision = 10, scale = 0)
  @NotNull
  private Long id;

  @Column
  private String requestURI;

  @Column
  private String queryString;

  @Column
  private String headerNames;

  @Column
  private int contentLength;

  @Column
  private String contentType;

  @Column
  private String remoteAddr;

  @Column
  private String remoteUser;

  @Column
  private String remoteHost;

  @Column
  private int remotePort;

  @Column
  private String protocol;

  @Column
  private String pathInfo;

  @Column
  private String body;

  @Column
  private LocalDateTime creation;

  public PaypalRequest() {
    creation = LocalDateTime.now();
  }

  public PaypalRequest(String requestURI, String queryString, String headerNames, int contentLength,
      String contentType, String remoteAddr, String remoteUser, String remoteHost,
      int remotePort, String protocol, String pathInfo, String body) {
    this();
    this.requestURI = requestURI;
    this.queryString = queryString;
    this.headerNames = headerNames;
    this.contentLength = contentLength;
    this.contentType = contentType;
    this.remoteAddr = remoteAddr;
    this.remoteUser = remoteUser;
    this.remoteHost = remoteHost;
    this.remotePort = remotePort;
    this.protocol = protocol;
    this.pathInfo = pathInfo;
    this.body = body;
  }

  public PaypalRequest(HttpServletRequest request) throws IOException {
    this(request.getRequestURI(),
        request.getQueryString(),
        getHeaderNames(request),
        request.getContentLength(),
        request.getContentType(),
        request.getRemoteAddr(),
        request.getRemoteUser(),
        request.getRemoteHost(),
        request.getRemotePort(),
        request.getProtocol(),
        request.getPathInfo(),
        getBody(request.getInputStream())
    );
  }

  private static String getBody(ServletInputStream inputStream) throws IOException {
    return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
  }

  private static String getHeaderNames(HttpServletRequest request) {
    return Collections.list(request.getHeaderNames()).stream()
        .map(headername -> headername + "=" + request.getHeader(headername))
        .collect(Collectors.joining("\n"));
  }

  public String getBody() {
    return body;
  }
}
