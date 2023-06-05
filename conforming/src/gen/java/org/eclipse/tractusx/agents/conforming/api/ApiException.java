//
// Conforming Agent Implementation
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package org.eclipse.tractusx.agents.conforming.api;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2023-03-23T11:28:11.277776230Z[GMT]")public class ApiException extends Exception{
    private int code;
    public ApiException (int code, String msg) {
        super(msg);
        this.code = code;
    }
}
