package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import ru.yandex.qatools.htmlelements.exceptions.HtmlElementsException;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementFieldAnnotationsHandler;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.WildcardType;

import static ru.yandex.qatools.htmlelements.utils.HtmlElementUtils.getGenericParameterClass;

public class CustomHtmlElementFieldAnnotationsHandler extends HtmlElementFieldAnnotationsHandler {

    protected Class<?> genericParameter;

    public CustomHtmlElementFieldAnnotationsHandler(Field field, Class<?> genericParameter) {
        super(field);
        this.genericParameter = genericParameter;
    }

    @Override
    public By buildBy() {
        if (HtmlElementBlockDecorator.isHtmlElementBlockList(getField(), genericParameter)) {
            return buildByFromHtmlElementListAnnotations();
        }
        return super.buildBy();
    }

    private By buildByFromHtmlElementListAnnotations() {
        assertValidAnnotations();

        By result = buildByFromFindAnnotations();
        if (result != null) {
            return result;
        }

        Class<?> listParameterClass;
        if (((ParameterizedTypeImpl) getField().getGenericType()).getActualTypeArguments()[0] instanceof WildcardType && genericParameter != null) {
            listParameterClass = genericParameter;
        } else {
            listParameterClass = getGenericParameterClass(getField());
        }

        while (listParameterClass != Object.class) {
            if (listParameterClass.isAnnotationPresent(FindBy.class)) {
                return new FindBy.FindByBuilder().buildIt(listParameterClass.getAnnotation(FindBy.class), null);
            }
            listParameterClass = listParameterClass.getSuperclass();
        }

        throw new HtmlElementsException(String.format("Cannot determine how to locate element %s", getField()));
    }

    private By buildByFromFindAnnotations() {
        if (getField().isAnnotationPresent(FindBys.class)) {
            FindBys findBys = getField().getAnnotation(FindBys.class);
            return new FindBys.FindByBuilder().buildIt(findBys, null);
        }

        if (getField().isAnnotationPresent(FindAll.class)) {
            FindAll findAll = getField().getAnnotation(FindAll.class);
            return new FindAll.FindByBuilder().buildIt(findAll, null);
        }

        if (getField().isAnnotationPresent(FindBy.class)) {
            FindBy findBy = getField().getAnnotation(FindBy.class);
            return new FindBy.FindByBuilder().buildIt(findBy, null);
        }
        return null;
    }
}
