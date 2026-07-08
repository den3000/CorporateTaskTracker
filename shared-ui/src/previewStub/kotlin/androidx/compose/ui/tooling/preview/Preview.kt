package androidx.compose.ui.tooling.preview

// Пустая аннотация, которая ничего не делает, но позволяет коду скомпилироваться
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION
)
@Retention(AnnotationRetention.BINARY)
annotation class Preview
