package pages;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import pages.htmlelements.blocks.BaseBlock;
import ru.yandex.qatools.htmlelements.exceptions.HtmlElementsException;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.pagefactory.CustomElementLocatorFactory;
import ru.yandex.qatools.htmlelements.utils.HtmlElementUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.*;
import java.util.List;

import static ru.yandex.qatools.htmlelements.loader.decorator.ProxyFactory.createHtmlElementListProxy;
import static ru.yandex.qatools.htmlelements.utils.HtmlElementUtils.*;

public class HtmlElementBlockDecorator<P extends BasePage> extends HtmlElementDecorator {

    protected P page;
    protected Class<?> genericParameter;

    public HtmlElementBlockDecorator(CustomElementLocatorFactory factory, P page, Class<?> genericParameter) {
        super(factory);
        this.page = page;
        this.genericParameter = genericParameter;
    }

    public static boolean isParameterizedBlock(Field field) {
        return BaseBlock.class.isAssignableFrom(field.getType()) && hasGenericParameter(field);
    }

    public static boolean isHtmlElementBlockList(Field field, Class<?> genericParameter) {
        if (!isParametrizedList(field)) {
            return false;
        }

        if (((ParameterizedTypeImpl) field.getGenericType()).getActualTypeArguments()[0] instanceof WildcardType && genericParameter != null) {
            return isHtmlElementBlock(genericParameter);
        } else {
            Class listParameterClass = HtmlElementUtils.getGenericParameterClass(field);
            return isHtmlElementBlock(listParameterClass);
        }
    }

    public static boolean isHtmlElementBlock(Class<?> clazz) {
        return BaseBlock.class.isAssignableFrom(clazz);
    }

    private static boolean isParametrizedList(Field field) {
        return isList(field) && hasGenericParameter(field);
    }

    private static boolean isList(Field field) {
        return List.class.isAssignableFrom(field.getType());
    }

    private static boolean hasGenericParameter(Field field) {
        return field.getGenericType() instanceof ParameterizedType;
    }

    public static <P extends BasePage> void populatePageObject(Object instance, SearchContext searchContext, BasePage page, Class<?> genericParameter) {
        populatePageObject(instance, new CustomHtmlElementLocatorFactory(searchContext, genericParameter), page, genericParameter);
    }

    /**
     * Initializes fields of the given page object using specified locator factory.
     *
     * @param page           Page object to be initialized.
     * @param locatorFactory Locator factory that will be used to locate elements.
     */
    public static <P extends BasePage> void populatePageObject(Object instance, CustomElementLocatorFactory locatorFactory, BasePage page, Class<?> genericParameter) {
        PageFactory.initElements(new HtmlElementBlockDecorator<>(locatorFactory, page, genericParameter), instance);
    }

    public static <T extends BaseBlock<P>, P extends BasePage> T createHtmlElementBlock(
            Class<T> elementClass, WebElement elementToWrap, String name, P page
    ) {
        try {
            T instance = newInstance(elementClass);
            instance.setWrappedElement(elementToWrap);
            instance.setName(name);
            instance.init(page);
            // Recursively initialize elements of the block
            populatePageObject(instance, elementToWrap, page, null);
            return instance;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            throw new HtmlElementsException(e);
        }
    }

    public static <T extends BaseBlock<P>, P extends BasePage> T createParameterizedBlock(
            Class<T> elementClass, WebElement elementToWrap, String name, P page, Class<?> genericParameter
    ) {
        try {
            T instance = newInstance(elementClass);
            instance.setWrappedElement(elementToWrap);
            instance.setName(name);
            instance.init(page);
            // Recursively initialize elements of the block
            populatePageObject(instance, elementToWrap, page, genericParameter);
            return instance;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            throw new HtmlElementsException(e);
        }
    }

    @Override
    public Object decorate(ClassLoader loader, Field field) {
        try {
            if (isParameterizedBlock(field)) {
                return decorateParameterizedBlock(loader, field, page);
            }
            if (isHtmlElementBlock(field.getType())) {
                return decorateHtmlElementBlock(loader, field, page);
            }
            if (isHtmlElementBlockList(field, genericParameter)) {
                return decorateHtmlElementBlockList(loader, field, page);
            }
            return super.decorate(loader, field);
        } catch (ClassCastException e) {
            return null;
        }
    }

    protected <T extends BaseBlock<P>> T decorateHtmlElementBlock(ClassLoader loader, Field field, P page) {
        WebElement elementToWrap = decorateWebElement(loader, field);
        //noinspection unchecked
        return createHtmlElementBlock((Class<T>) field.getType(), elementToWrap, getElementName(field), page);
    }

    protected <T extends BaseBlock<P>> T decorateParameterizedBlock(ClassLoader loader, Field field, P page) {
        WebElement elementToWrap = decorateWebElement(loader, field);
        Class<?> genericParameterClass = getGenericParameterClass(field);
        //noinspection unchecked
        return createParameterizedBlock((Class<T>) field.getType(), elementToWrap, getElementName(field), page, genericParameterClass);
    }

    protected <T extends BaseBlock<P>> List<T> decorateHtmlElementBlockList(ClassLoader loader, Field field, P page) {
        @SuppressWarnings("unchecked")
        Class<T> elementClass;
        if (((ParameterizedTypeImpl) field.getGenericType()).getActualTypeArguments()[0] instanceof WildcardType) {
            //noinspection unchecked
            elementClass = (Class<T>) genericParameter;
        } else {
            //noinspection unchecked
            elementClass = (Class<T>) getGenericParameterClass(field);
        }


        ElementLocator locator = factory.createLocator(field);
        String name = getElementName(field);

        InvocationHandler handler = new HtmlElementBlockListNamedProxyHandler<>(elementClass, locator, name, page);

        return createHtmlElementListProxy(loader, handler);
    }

}
