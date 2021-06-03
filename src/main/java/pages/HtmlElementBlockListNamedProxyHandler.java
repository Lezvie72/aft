package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import pages.htmlelements.blocks.BaseBlock;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import static pages.HtmlElementBlockDecorator.createHtmlElementBlock;

public class HtmlElementBlockListNamedProxyHandler<T extends BaseBlock<P>, P extends BasePage> implements InvocationHandler {

    private final Class<T> elementClass;
    private final ElementLocator locator;
    private final String name;
    private final P page;

    public HtmlElementBlockListNamedProxyHandler(Class<T> elementClass, ElementLocator locator, String name, P page) {
        this.elementClass = elementClass;
        this.locator = locator;
        this.name = name;
        this.page = page;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if ("toString".equals(method.getName())) {
            return name;
        }

        List<T> elements = new LinkedList<>();
        int elementNumber = 0;
        for (WebElement element : locator.findElements()) {
            String newName = String.format("%s [%d]", name, elementNumber++);
            elements.add(createHtmlElementBlock(elementClass, element, newName, page));
        }

        try {
            return method.invoke(elements, objects);
        } catch (InvocationTargetException e) {
            // Unwrap the underlying exception
            throw e.getCause();
        }
    }
}