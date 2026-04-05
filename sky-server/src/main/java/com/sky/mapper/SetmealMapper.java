package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * create new set
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 停售关联套餐
     * @param dishId
     */
    void disableByDishId(Long dishId);

    /**
     * 根据id修改套餐
     *
     * @param setmeal
     */
    void update(Setmeal setmeal);

    /**
     * Dynamic query
     * @param setmeal 套餐
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * from setmealID to query dish item
     * @param setmealId
     * @return
     */
    @Select("select sd.name, sd.copies, d.image, d.description" +
    "from setmeal_dish sd left join dish d on sd.dish_id = d.id" +
    "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

    /**
     * 根据id查询数据
     * @param id
     * @return
     */
    Setmeal getById(Long id);

    /**
     * 根据id删除数据
     * @param id
     */
    void deleteById(Long id);

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
}
