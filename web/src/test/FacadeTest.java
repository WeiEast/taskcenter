import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;
import com.treefinance.saas.taskcenter.web.TaskCenterApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author chengtong
 * @date 18/4/12 16:25
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TaskCenterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class FacadeTest {

    @Autowired
    TaskFacade taskFacade;


    @Test
    public void testTestAop() {
        TaskResult<Object> result = taskFacade.testAop("hao", "aaa");
        System.out.println(result.toString());
    }

}