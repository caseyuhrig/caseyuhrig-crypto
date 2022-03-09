package caseyuhrig.crypto;

public class EmptyParameterException extends RuntimeException
{
    private final String parameterName;


    public EmptyParameterException(final String parameterName)
    {
        super();
        this.parameterName = parameterName;
    }


    public String getParameterName()
    {
        return parameterName;
    }
}
