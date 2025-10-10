package cloud.catfish.mbg.comm;

public class CommonConfig {

    // project absolute path
    public static String PROJECT_ABSOLUTE_PATH = "/Users/qiupan/Documents/GitHub/spring-quick-start-generator";

    // module absolute path
    public static String MBG_MODULE_ABSOLUTE_PATH = PROJECT_ABSOLUTE_PATH + "/mbg";
    public static String COMMON_MODULE_ABSOLUTE_PATH = PROJECT_ABSOLUTE_PATH + "/common";
    public static String ADMIN_MODULE_ABSOLUTE_PATH = PROJECT_ABSOLUTE_PATH + "/admin";

    // package name
    public static String ROOT_PACKAGE = "cloud.catfish.mbg";
    public static String MAPPER_PACKAGE_NAME = ROOT_PACKAGE + ".mapper";
    public static String SERVICE_PACKAGE_NAME = ROOT_PACKAGE + ".service";
    public static String SERVICE_IMPL_PACKAGE_NAME = ROOT_PACKAGE + ".service.impl";
    public static String CONTROLLER_PACKAGE_NAME = ROOT_PACKAGE + ".controller";
    public static String CONVERTER_PACKAGE_NAME = ROOT_PACKAGE + ".converter";
    public static String REQUEST_PARAM_PACKAGE_NAME = ROOT_PACKAGE + ".request";
    public static String DOMAIN_PACKAGE_NAME = ROOT_PACKAGE + ".domain";
    public static String VO_PACKAGE_NAME = ROOT_PACKAGE + ".vo";


    // package relative path                              RELATIVE
    public static String ROOT_RELATIVE_PATH = "cloud/catfish/mbg";
    public static String MAPPER_PACKAGE_RELATIVE_NAME = ROOT_RELATIVE_PATH + "/mapper";
    public static String SERVICE_PACKAGE_RELATIVE_NAME = ROOT_RELATIVE_PATH + "/service";
    public static String SERVICE_IMPL_PACKAGE_RELATIVE_NAME = ROOT_RELATIVE_PATH + "/service.impl";
    public static String CONTROLLER_PACKAGE_RELATIVE_NAME = ROOT_RELATIVE_PATH + "/controller";
    public static String CONVERTER_PACKAGE_RELATIVE_NAME = ROOT_RELATIVE_PATH + "/converter";
    public static String REQUEST_PARAM_PACKAGE_RELATIVE_NAME = ROOT_RELATIVE_PATH + "/request";
    public static String DOMAIN_PACKAGE_RELATIVE_NAME = ROOT_RELATIVE_PATH + "/domain";
    public static String VO_PACKAGE_RELATIVE_NAME = ROOT_RELATIVE_PATH + "/vo";

    // file suffix name
    public static String MAPPER_SUFFIX_FILE_NAME = "Mapper";
    public static String SERVICE_SUFFIX_FILE_NAME = "Service";
    public static String SERVICE_SUFFIX_IMPL_FILE_NAME = "ServiceImpl";
    public static String CONTROLLER_SUFFIX_FILE_NAME = "Controller";
    public static String CONVERTER_SUFFIX_FILE_NAME = "Converter";
    public static String REQUEST_SUFFIX_PARAM_FILE_NAME = "Req";
    public static String DOMAIN_SUFFIX_FILE_NAME = "";
    public static String VO_SUFFIX_FILE_NAME = "VO";
    public static String MAPSTRUCT_SUFFIX_FILE_NAME = "Converter";

    // default template
    public static String RESOURCES_ABSOLUTE_PATH = "/Users/qiupan/Documents/GitHub/spring-quick-start-generator/mbg/src/main/resources/templates";
    public static String SERVICE_TEMPLATE_PATH = RESOURCES_ABSOLUTE_PATH + "/service.vm";
    public static String SERVICE_IMPL_TEMPLATE_PATH = RESOURCES_ABSOLUTE_PATH + "/serviceImpl.vm";
    public static String CONTROLLER_TEMPLATE_PATH = RESOURCES_ABSOLUTE_PATH + "/controller.vm";


}
