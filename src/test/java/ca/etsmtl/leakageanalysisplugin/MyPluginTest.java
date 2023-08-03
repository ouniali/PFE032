package ca.etsmtl.leakageanalysisplugin;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class MyPluginTest extends BasePlatformTestCase {

    public void testRename() {
        myFixture.testRename("foo.xml", "foo_after.xml", "a2");
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/rename";
    }
}