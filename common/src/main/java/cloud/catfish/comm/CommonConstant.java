package cloud.catfish.comm;

public class CommonConstant {

    // project absolute path
    public static String PROJECT_ABSOLUTE_PATH = "C:\\Users\\feemu\\Documents\\GitHub\\spring-quick-start-generator";

    public static String OUTPUT_ABSOLUTE_PATH = PROJECT_ABSOLUTE_PATH + "/generated";

    // module absolute path
    public static String MBG_MODULE_ABSOLUTE_PATH = PROJECT_ABSOLUTE_PATH + "/mbg";
    public static String COMMON_MODULE_ABSOLUTE_PATH = PROJECT_ABSOLUTE_PATH + "/common";
    public static String ADMIN_MODULE_ABSOLUTE_PATH = PROJECT_ABSOLUTE_PATH + "/admin";

    public static String RESOURCES_RELATIVE_PATH = "";
    public static String SOURCE_CODE_RELATIVE_PATH = "";

    // package name
    public static String ROOT_PACKAGE = "cloud.catfish.mbg";
    public static String MAPPER_PACKAGE_NAME = ROOT_PACKAGE + ".mapper";
    public static String SERVICE_PACKAGE_NAME = ROOT_PACKAGE + ".service";
    public static String SERVICE_IMPL_PACKAGE_NAME = ROOT_PACKAGE + ".service.impl";
    public static String CONTROLLER_PACKAGE_NAME = ROOT_PACKAGE + ".controller";
    public static String CONVERTER_PACKAGE_NAME = ROOT_PACKAGE + ".converter";
    public static String REQUEST_PARAM_PACKAGE_NAME = ROOT_PACKAGE + ".request";
    public static String MODEL_PACKAGE_NAME = ROOT_PACKAGE + ".model";
    public static String VO_PACKAGE_NAME = ROOT_PACKAGE + ".vo";


    // package relative path                              RELATIVE
    public static String ROOT_RELATIVE_PATH = "/cloud/catfish/mbg";
    public static String MAPPER_PACKAGE_RELATIVE_NAME = "";
    public static String SERVICE_PACKAGE_RELATIVE_NAME = "";
    public static String SERVICE_IMPL_PACKAGE_RELATIVE_NAME = "";
    public static String CONTROLLER_PACKAGE_RELATIVE_NAME = "";
    public static String CONVERTER_PACKAGE_RELATIVE_NAME = "";
    public static String REQUEST_PARAM_PACKAGE_RELATIVE_NAME = "";
    public static String DOMAIN_PACKAGE_RELATIVE_NAME = "";
    public static String VO_PACKAGE_RELATIVE_NAME = "";

    // file suffix name
    public static String MAPPER_SUFFIX_FILE_NAME = "Mapper";
    public static String SERVICE_SUFFIX_FILE_NAME = "Service";
    public static String SERVICE_IMPL_SUFFIX_FILE_NAME = "ServiceImpl";
    public static String CONTROLLER_SUFFIX_FILE_NAME = "Controller";
    public static String CONVERTER_SUFFIX_FILE_NAME = "Converter";
    public static String REQUEST_SUFFIX_PARAM_FILE_NAME = "Req";
    public static String DOMAIN_SUFFIX_FILE_NAME = "";
    public static String VO_SUFFIX_FILE_NAME = "VO";
    public static String MAPSTRUCT_SUFFIX_FILE_NAME = "Converter";

    // default template
    public static String RESOURCES_CLASS_PATH = "templates";
    public static String SERVICE_TEMPLATE_PATH = RESOURCES_CLASS_PATH + "/service.vm";
    public static String SERVICE_IMPL_TEMPLATE_PATH = RESOURCES_CLASS_PATH + "/serviceImpl.vm";
    public static String CONTROLLER_TEMPLATE_PATH = RESOURCES_CLASS_PATH + "/controller.vm";

    // 时间格式
    public static String DEFAULT_JSON_FORMAT = "@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone = \"GMT+8\")";
    public static String DEFAULT_DATE_TIME_FORMAT = "@DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")";
}
