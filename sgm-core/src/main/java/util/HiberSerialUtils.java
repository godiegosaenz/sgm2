/*
 *  Origami Solutions
 */

package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import org.hibernate.Hibernate;
import org.hibernate.type.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fernando
 */
public final class HiberSerialUtils {
    
    private static final Logger log = LoggerFactory.getLogger( HiberSerialUtils.class );

	private HiberSerialUtils() {
	}

	// Clone
	//-----------------------------------------------------------------------

	/**
	 * <p>Deep clone an <code>Object</code> using serialization.</p>
	 *
	 * <p>This is many times slower than writing clone methods by hand
	 * on all objects in your object graph. However, for complex object
	 * graphs, or for those that don't support deep cloning this can
	 * be a simple alternative implementation. Of course all the objects
	 * must be <code>Serializable</code>.</p>
	 *
	 * @param object the <code>Serializable</code> object to clone
	 *
	 * @return the cloned object
	 *
	 * @throws SerializationException (runtime) if the serialization fails
	 */
	public static Object clone(Serializable object) throws SerializationException {
		log.trace( "Starting clone through serialization" );
		if ( object == null ) {
			return null;
		}
		return deserialize( serialize( object ), object.getClass().getClassLoader() );
	}

	// Serialize
	//-----------------------------------------------------------------------

	/**
	 * <p>Serializes an <code>Object</code> to the specified stream.</p>
	 *
	 * <p>The stream will be closed once the object is written.
	 * This avoids the need for a finally clause, and maybe also exception
	 * handling, in the application code.</p>
	 *
	 * <p>The stream passed in is not buffered internally within this method.
	 * This is the responsibility of your application if desired.</p>
	 *
	 * @param obj the object to serialize to bytes, may be null
	 * @param outputStream the stream to write to, must not be null
	 *
	 * @throws IllegalArgumentException if <code>outputStream</code> is <code>null</code>
	 * @throws SerializationException (runtime) if the serialization fails
	 */
	public static void serialize(Serializable obj, OutputStream outputStream) throws SerializationException {
		if ( outputStream == null ) {
			throw new IllegalArgumentException( "The OutputStream must not be null" );
		}

		if ( log.isTraceEnabled() ) {
			if ( Hibernate.isInitialized( obj ) ) {
				log.trace( "Starting serialization of object [" + obj + "]" );
			}
			else {
				log.trace( "Starting serialization of [uninitialized proxy]" );
			}
		}

		ObjectOutputStream out = null;
		try {
			// stream closed in the finally
			out = new ObjectOutputStream( outputStream );
			out.writeObject( obj );

		}
		catch ( IOException ex ) {
			throw new SerializationException( "could not serialize", ex );
		}
		finally {
			try {
				if ( out != null ) {
					out.close();
				}
			}
			catch ( IOException ignored ) {
			}
		}
	}

	/**
	 * <p>Serializes an <code>Object</code> to a byte array for
	 * storage/serialization.</p>
	 *
	 * @param obj the object to serialize to bytes
	 *
	 * @return a byte[] with the converted Serializable
	 *
	 * @throws SerializationException (runtime) if the serialization fails
	 */
	public static byte[] serialize(Serializable obj) throws SerializationException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream( 512 );
		serialize( obj, byteArrayOutputStream );
		return byteArrayOutputStream.toByteArray();
	}

	// Deserialize
	//-----------------------------------------------------------------------

	/**
	 * Deserializes an object from the specified stream using the Thread Context
	 * ClassLoader (TCCL).
	 * <p/>
	 * Delegates to {@link #doDeserialize}
	 *
	 * @param inputStream the serialized object input stream, must not be null
	 *
	 * @return the deserialized object
	 *
	 * @throws IllegalArgumentException if <code>inputStream</code> is <code>null</code>
	 * @throws SerializationException (runtime) if the serialization fails
	 */
	public static Object deserialize(InputStream inputStream) throws SerializationException {
		return doDeserialize( inputStream, defaultClassLoader(), hibernateClassLoader(), null );
	}

	/**
	 * Returns the Thread Context ClassLoader (TCCL).
	 *
	 * @return The current TCCL
	 */
	public static ClassLoader defaultClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	public static ClassLoader hibernateClassLoader() {
		return HiberSerialUtils.class.getClassLoader();
	}

	/**
	 * Deserializes an object from the specified stream using the Thread Context
	 * ClassLoader (TCCL).  If there is no TCCL set, the classloader of the calling
	 * class is used.
	 * <p/>
	 * The stream will be closed once the object is read. This avoids the need
	 * for a finally clause, and maybe also exception handling, in the application
	 * code.
	 * <p/>
	 * The stream passed in is not buffered internally within this method.  This is
	 * the responsibility of the caller, if desired.
	 *
	 * @param inputStream the serialized object input stream, must not be null
	 * @param loader The classloader to use
	 *
	 * @return the deserialized object
	 *
	 * @throws IllegalArgumentException if <code>inputStream</code> is <code>null</code>
	 * @throws SerializationException (runtime) if the serialization fails
	 */
	public static Object deserialize(InputStream inputStream, ClassLoader loader) throws SerializationException {
		return doDeserialize( inputStream, loader, defaultClassLoader(), hibernateClassLoader() );
	}

	public static Object doDeserialize(
			InputStream inputStream,
			ClassLoader loader,
			ClassLoader fallbackLoader1,
			ClassLoader fallbackLoader2) throws SerializationException {
		if ( inputStream == null ) {
			throw new IllegalArgumentException( "The InputStream must not be null" );
		}

		log.trace( "Starting deserialization of object" );

		try {
			CustomObjectInputStream in = new CustomObjectInputStream(
					inputStream,
					loader,
					fallbackLoader1,
					fallbackLoader2
			);
			try {
				return in.readObject();
			}
			catch ( ClassNotFoundException e ) {
				throw new SerializationException( "could not deserialize", e );
			}
			catch ( IOException e ) {
				throw new SerializationException( "could not deserialize", e );
			}
			finally {
				try {
					in.close();
				}
				catch ( IOException ignore ) {
					// ignore
				}
			}
		}
		catch ( IOException e ) {
			throw new SerializationException( "could not deserialize", e );
		}
	}

	/**
	 * Deserializes an object from an array of bytes using the Thread Context
	 * ClassLoader (TCCL).  If there is no TCCL set, the classloader of the calling
	 * class is used.
	 * <p/>
	 * Delegates to {@link #deserialize(byte[], ClassLoader)}
	 *
	 * @param objectData the serialized object, must not be null
	 *
	 * @return the deserialized object
	 *
	 * @throws IllegalArgumentException if <code>objectData</code> is <code>null</code>
	 * @throws SerializationException (runtime) if the serialization fails
	 */
	public static Object deserialize(byte[] objectData) throws SerializationException {
		return doDeserialize( wrap( objectData ), defaultClassLoader(), hibernateClassLoader(), null );
	}

	private static InputStream wrap(byte[] objectData) {
		if ( objectData == null ) {
			throw new IllegalArgumentException( "The byte[] must not be null" );
		}
		return new ByteArrayInputStream( objectData );
	}

	/**
	 * Deserializes an object from an array of bytes.
	 * <p/>
	 * Delegates to {@link #deserialize(java.io.InputStream, ClassLoader)} using a
	 * {@link ByteArrayInputStream} to wrap the array.
	 *
	 * @param objectData the serialized object, must not be null
	 * @param loader The classloader to use
	 *
	 * @return the deserialized object
	 *
	 * @throws IllegalArgumentException if <code>objectData</code> is <code>null</code>
	 * @throws SerializationException (runtime) if the serialization fails
	 */
	public static Object deserialize(byte[] objectData, ClassLoader loader) throws SerializationException {
		return doDeserialize( wrap( objectData ), loader, defaultClassLoader(), hibernateClassLoader() );
	}


	/**
	 * By default, to resolve the classes being deserialized JDK serialization uses the
	 * classes loader which loaded the class which initiated the deserialization call.  Here
	 * that would be hibernate classes.  However, there are cases where that is not the correct
	 * class loader to use; mainly here we are worried about deserializing user classes in
	 * environments (app servers, etc) where Hibernate is on a parent classes loader.  To
	 * facilitate for that we allow passing in the class loader we should use.
	 */
	private static final class CustomObjectInputStream extends ObjectInputStream {
		private final ClassLoader loader1;
		private final ClassLoader loader2;
		private final ClassLoader loader3;

		private CustomObjectInputStream(
				InputStream in,
				ClassLoader loader1,
				ClassLoader loader2,
				ClassLoader loader3) throws IOException {
			super( in );
			this.loader1 = loader1;
			this.loader2 = loader2;
			this.loader3 = loader3;
		}

		/**
		 * {@inheritDoc}
		 */
		protected Class resolveClass(ObjectStreamClass v) throws IOException, ClassNotFoundException {
			String className = v.getName();
                        System.out.println(" Clase a deserializar: " + className);
			log.trace( "Attempting to locate class [" + className + "]" );

			try {
				return Class.forName( className, false, loader1 );
			}
			catch ( ClassNotFoundException e ) {
				log.trace( "Unable to locate class using given classloader" );
			}

			if ( different( loader1, loader2 ) ) {
				try {
					return Class.forName( className, false, loader2 );
				}
				catch ( ClassNotFoundException e ) {
					log.trace( "Unable to locate class using given classloader" );
				}
			}

			if ( different( loader1, loader3 ) && different( loader2, loader3 ) ) {
				try {
					return Class.forName( className, false, loader3 );
				}
				catch ( ClassNotFoundException e ) {
					log.trace( "Unable to locate class using given classloader" );
				}
			}

			// By default delegate to normal JDK deserialization which will use the class loader
			// of the class which is calling this deserialization.
			return super.resolveClass( v );
		}

		private boolean different(ClassLoader one, ClassLoader other) {
			if ( one == null ) {
				return other != null;
			}
			else {
				return !one.equals( other );
			}
		}
	}
    
}
