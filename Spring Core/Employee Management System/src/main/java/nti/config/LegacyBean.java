package nti.config;

public class LegacyBean {
    public LegacyBean() {
        System.out.println("[LegacyBean] constructor");
    }
    public void defaultInit()    { System.out.println("[LegacyBean] defaultInit()"); }
    public void defaultDestroy() { System.out.println("[LegacyBean] defaultDestroy()"); }

    public void init()    { System.out.println("[LegacyBean] init()"); }
    public void destroy() { System.out.println("[LegacyBean] destroy()"); }
}