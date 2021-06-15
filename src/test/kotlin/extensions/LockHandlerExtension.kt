package extensions

import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.parallel.ResourceLock

class LockHandlerExtension : BeforeEachCallback {

    override fun beforeEach(context: ExtensionContext?) {
        val method = context?.testMethod
        val clz = context?.testClass
        if (method?.isPresent == true) {
            val m = method.get()
            if (m.isAnnotationPresent(ResourceLock::class.java)) {
                handleLocks(m.getAnnotation(ResourceLock::class.java))
            }
        }
        if (clz?.isPresent == true) {
            val c = clz.get()
            if (c.isAnnotationPresent(ResourceLock::class.java)) {
                handleLocks(c.getAnnotation(ResourceLock::class.java))
            }
        }
    }

    private fun handleLocks(lock: ResourceLock) {
        if (lock.value == utils.Constants.REGISTRATION_LOCK) {

        }
    }
}