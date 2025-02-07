package cloud.catfish.mbg.service;

import cloud.catfish.mbg.model.UmsAdmin;

import java.util.List;

/**
 * @description: ${tableRemark}服务层接口
 */
public interface IUmsAdminService {

    /**
     * 分页查询后台
     *
     * @param pageNum  当前页码
     * @param pageSize 每页显示数量
     * @return 查询结果列表
     */
    List<UmsAdmin> page(Integer pageNum, Integer pageSize);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 查询结果
     */
    UmsAdmin selectByPrimaryKey(Long id);

    /**
     * 根据主键更新
     *
     * @param record 更新的数据对象
     * @return 是否更新成功
     */
    Boolean updateByPrimaryKeySelective(UmsAdmin record);

    /**
     * 根据条件更新
     *
     * @param record 数据对象，包含更新的数据和条件
     * @return 影响行数
     */
    int updateByExampleSelective(UmsAdmin record);

    /**
     * 根据主键删除
     *
     * @param id 主键ID
     * @return 是否删除成功
     */
    Boolean deleteByPrimaryKey(Long id);

    /**
     * 根据条件删除
     *
     * @param record 删除条件
     * @return 影响行数
     */
    int deleteByExample(UmsAdmin record);

    /**
     * 新增
     *
     * @param record 新增的数据对象
     * @return 是否新增成功
     */
    Boolean insertSelective(UmsAdmin record);

}
