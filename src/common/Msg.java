package common;

import java.io.Serializable;

public class Msg implements Serializable
{	
    static final long serialVersionUID = 1L;
    protected String name;
    protected String msg;

    public Msg(String name, String msg)
    {
            this.name = name;
            this.msg = msg;
    }

    public String getName(){ return name; }

    public String getMsg(){ return msg; 
    }    
}
