package indi.kennhuang.rfidwatchdog.server;


import indi.kennhuang.rfidwatchdog.server.module.ModuleTestSuite;
import indi.kennhuang.rfidwatchdog.server.module.TestUser;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(value = Suite.class)
@Suite.SuiteClasses(value={ModuleTestSuite.class})
public class TestSuite {

}
