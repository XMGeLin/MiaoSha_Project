package com.imooc.miaosha.controller;

//请实现createObject方法，执行main函数达到注释中想要输出的效果

import java.lang.reflect.Proxy;

interface IA {
    String getHelloName();
}

public class ShowMeBug {
    public static void main(String[] arges) throws Exception{
        IA ia = (IA) createObject(IA.class.getName()+"$getHelloName=Abc");
        System.out.println(ia.getHelloName()); //方法名匹配的时候，输出“Abc”
        ia = (IA) createObject(IA.class.getName()+"$getTest=Bcd");
        System.out.println(ia.getHelloName()); //方法名不匹配的时候，输出null
    }

    //请实现方法createObject，接口中"getName()"方法名仅仅是个示例，不能写死判断

    public static Object createObject(String str) throws Exception {

        String className = str.substring(0, str.lastIndexOf("$"));
        String methodName = str.substring(str.lastIndexOf("$") + 1, str.lastIndexOf("=")).trim();
        String value = str.substring(str.indexOf("=") + 1).trim();
        System.out.println(Class.forName(className));
        System.out.println(Class.forName(className).getClassLoader());
        return Proxy.newProxyInstance(Class.forName(className).getClassLoader(),
                new Class[]{Class.forName(className)},
                ((proxy, method, args) -> {
                    if (method.getName().equals(methodName)) {
                        return value;
                    }else{
                        return null;
                    }
                }));
    }
}



