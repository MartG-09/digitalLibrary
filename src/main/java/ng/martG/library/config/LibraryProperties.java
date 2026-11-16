package ng.martG.library.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

/** Values from the "library.*" keys in application.properties. */
@ConfigurationProperties(prefix = "library")
public class LibraryProperties {

    private int loanDays = 14;
    private int maxActiveLoans = 5;
    private BigDecimal finePerDay = new BigDecimal("5");
    private String currencySymbol = "\u20B9";

    private String adminName = "Library Admin";
    private String adminEmail = "admin@library.com";
    private String adminPassword = "Admin@123";
    private boolean seedSampleBooks = true;

    public int getLoanDays() { return loanDays; }
    public void setLoanDays(int loanDays) { this.loanDays = loanDays; }

    public int getMaxActiveLoans() { return maxActiveLoans; }
    public void setMaxActiveLoans(int maxActiveLoans) { this.maxActiveLoans = maxActiveLoans; }

    public BigDecimal getFinePerDay() { return finePerDay; }
    public void setFinePerDay(BigDecimal finePerDay) { this.finePerDay = finePerDay; }

    public String getCurrencySymbol() { return currencySymbol; }
    public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }

    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public String getAdminPassword() { return adminPassword; }
    public void setAdminPassword(String adminPassword) { this.adminPassword = adminPassword; }

    public boolean isSeedSampleBooks() { return seedSampleBooks; }
    public void setSeedSampleBooks(boolean seedSampleBooks) { this.seedSampleBooks = seedSampleBooks; }
}
