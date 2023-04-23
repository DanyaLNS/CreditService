package mts.fintech.creditservice.exceptions;

public class TariffNotFoundException extends Exception{
    public TariffNotFoundException(){
        super("TARIFF_NOT_FOUND");
    }
}
