package indi.kennhuang.rfidwatchdog.server;

import indi.kennhuang.rfidwatchdog.server.devices.TestDoorUtil;
import indi.kennhuang.rfidwatchdog.server.devices.TestHardwareServer;
import indi.kennhuang.rfidwatchdog.server.module.TestUser;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(value = Suite.class)
@Suite.SuiteClasses(value={TestUser.class, TestDoorUtil.class})
public class TestSuite {

}
