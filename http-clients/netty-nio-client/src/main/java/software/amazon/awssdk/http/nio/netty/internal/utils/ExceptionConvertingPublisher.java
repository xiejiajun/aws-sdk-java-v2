package software.amazon.awssdk.http.nio.netty.internal.utils;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class ExceptionConvertingPublisher<T> implements Publisher<T> {
    private final Publisher<T> delegate;

    public ExceptionConvertingPublisher(Publisher<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void subscribe(Subscriber<? super T> s) {
        delegate.subscribe(new ExceptionConvertingSubscriber<>(s));
    }

    private static class ExceptionConvertingSubscriber<U> implements Subscriber<U> {
        private Subscriber<U> delegate;

        public ExceptionConvertingSubscriber(Subscriber<U> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void onSubscribe(Subscription s) {
            delegate.onSubscribe(s);
        }

        @Override
        public void onError(Throwable t) {
            delegate.onError(NettyExceptionConverter.convertNettyExceptionIntoHttpClientException(t));
        }

        @Override
        public void onComplete() {
            delegate.onComplete();
        }

        @Override
        public void onNext(U o) {
            delegate.onNext(o);
        }
    }
}
