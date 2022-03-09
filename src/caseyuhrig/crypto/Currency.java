package caseyuhrig.crypto;

import java.math.BigDecimal;
import java.math.MathContext;


public class Currency extends BigDecimal
{
    public Currency(final String val)
    {
        super(val, new MathContext(18));
    }
}
