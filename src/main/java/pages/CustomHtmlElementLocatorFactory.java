package pages;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.AjaxElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import ru.yandex.qatools.htmlelements.annotations.Timeout;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.WildcardType;

public class CustomHtmlElementLocatorFactory extends HtmlElementLocatorFactory {

    private final SearchContext searchContext;
    protected Class<?> genericParameter;

    public CustomHtmlElementLocatorFactory(SearchContext searchContext, Class<?> genericParameter) {
        super(searchContext);
        this.searchContext = searchContext;
        this.genericParameter = genericParameter;
    }

    @Override
    public ElementLocator createLocator(Field field) {
        return new AjaxElementLocator(searchContext, getTimeOut(field), new CustomHtmlElementFieldAnnotationsHandler(field, genericParameter));
    }

    @Override
    public int getTimeOut(Field field) {
        if (HtmlElementBlockDecorator.isHtmlElementBlockList(field, genericParameter) && ((ParameterizedTypeImpl) field.getGenericType()).getActualTypeArguments()[0] instanceof WildcardType && genericParameter != null) {
            if (field.isAnnotationPresent(Timeout.class)) {
                return field.getAnnotation(Timeout.class).value();
            }
            return super.getTimeOut(genericParameter);
        } else {
            return super.getTimeOut(field);
        }

    }
}
