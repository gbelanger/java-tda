package gb.tda.tools;

import hep.aida.ref.function.AbstractIFunction;

public class UserFunction extends AbstractIFunction {
    
    public UserFunction() {
        this("");
    }
    
    public UserFunction(String title) {
        super(title, 1, 1);
    }
    
    public UserFunction(String[] variableNames, String[] parameterNames) {
        super(variableNames, parameterNames);
    }
    
    public double value(double[] v) {
        return p[0]*v[0]*v[0];
    }
    
    // Here change the parameter names
    protected void init(String title) {
        for (int i=0; i<parameterNames.length; i++) { 
            parameterNames[i] = "UserFunctionParameter"+i; 
        }
        
        super.init(title);
    }
}
