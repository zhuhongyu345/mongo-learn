import com.zhuhongyu.pacific.boot.Application;
import com.zhuhongyu.pacific.dao.MrDao;
import com.zhuhongyu.pacific.dao.UniDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class TestSe {
    @Resource
    UniDao uniDao;
    @Resource
    MrDao mrDao;

    //    @Test
    public void test1() {
        uniDao.getMessageAll();
    }

    @Test
    public void test2() {
        mrDao.groupMr();
    }

}
