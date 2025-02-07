package cloud.catfish.mbg.service.impl;

import com.github.pagehelper.PageHelper;
import cloud.catfish.mbg.model.UmsAdmin;
import cloud.catfish.mbg.model.UmsAdminExample;
import cloud.catfish.mbg.mapper.UmsAdminMapper;
import cloud.catfish.mbg.service.UmsAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: 后台用户表服务层接口实现
 */
@Service
public class UmsAdminServiceImpl implements UmsAdminService {

    @Autowired
    private UmsAdminMapper umsAdminMapper;

    /**
     * 分页查询
     *
     * @param pageNum  当前页码
     * @param pageSize 每页显示数量
     * @return 查询结果列表
     */
    public List<UmsAdmin> page(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria();
        return umsAdminMapper.selectByExample(example);
    }

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 查询结果
     */
    public UmsAdmin selectByPrimaryKey(Long id) {
        return umsAdminMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据主键更新
     *
     * @param record 更新的数据对象
     * @return 是否更新成功
     */
    public Boolean updateByPrimaryKeySelective(UmsAdmin record) {
        return umsAdminMapper.updateByPrimaryKeySelective(record) > 0;
    }

    /**
     * 根据条件更新
     *
     * @param record 数据对象，包含更新的数据和条件
     * @return 影响行数
     */
    public int updateByExampleSelective(UmsAdmin record) {
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria();
        return umsAdminMapper.updateByExampleSelective(record, example);
    }

    /**
     * 根据主键删除
     *
     * @param id 主键ID
     * @return 是否删除成功
     */
    public Boolean deleteByPrimaryKey(Long id) {
        return umsAdminMapper.deleteByPrimaryKey(id) > 0;
    }

    /**
     * 根据条件删除
     *
     * @param record 删除条件
     * @return 影响行数
     */
    public int deleteByExample(UmsAdmin record) {
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria();
        return umsAdminMapper.deleteByExample(example);
    }

    /**
     * 新增
     *
     * @param record 新增的数据对象
     * @return 是否新增成功
     */
   public Boolean insertSelective(UmsAdmin record) {
       return umsAdminMapper.insertSelective(record) > 0;
   }

}
