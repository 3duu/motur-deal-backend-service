package br.com.motur.dealbackendservice.common.utils;

import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

@Component
public class StackTracerToStringHelper {

    public String transformStackTraceToString(Throwable throwable){
        return getStackTrace(throwable).replace("\r\n\t", "<br/><br/>");
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
