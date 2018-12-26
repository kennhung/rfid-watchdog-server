package indi.kennhuang.rfidwatchdog.server.module;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(value = Suite.class)
@Suite.SuiteClasses(value={TestUser.class, TestDoor.class, TestGroup.class, TestSetting.class})
public class ModuleTestSuite {

}
