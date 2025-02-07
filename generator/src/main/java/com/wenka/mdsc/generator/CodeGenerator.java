package com.wenka.mdsc.generator;


import com.wenka.mdsc.generator.chain.FileChain;
import com.wenka.mdsc.generator.context.GeneratorContext;
import com.wenka.mdsc.generator.model.TableInfo;
import com.wenka.mdsc.generator.resolve.BeanLoadResolve;
import com.wenka.mdsc.generator.service.GenFileService;
import com.wenka.mdsc.generator.util.FolderUtil;
import com.wenka.mdsc.generator.util.XmlUtil;

import java.util.List;
import java.util.Map;

/**
 * Created with IDEA
 *
 * @author wenka wkwenka@gmail.com
 * @date 2020/03/23  下午 02:57
 * @description:
 */
public class CodeGenerator {

    public static void main(String[] args) {
        gen();
    }

    private static void init() {
        // 初始化XML配置
        System.err.println("init xml setting");
        XmlUtil.readXml();

        // 初始化全局配置
        System.err.println();
        System.err.println("init global setting");
        FolderUtil.readPackageValue();

        // 初始化所有bean
        BeanLoadResolve.getInstance().load();
    }

    /**
     * 生成文件
     */
    public static void gen() {
        // 加载bean 读取配置
        init();

        // 注册责任链
        FileChain fileChain = null;
        List<GenFileService> beans = GeneratorContext.getBeans(GenFileService.class);

        // 生成文件
        Map<String, TableInfo> allTable = GeneratorContext.getAllTable();
        for (String table : allTable.keySet()) {
            System.out.println("### 进行【" + table + " 】表文件生成：");
            fileChain = new FileChain();
            TableInfo tableInfo = allTable.get(table);
            fileChain.addChain(beans).execute(tableInfo);
        }

    }
}
