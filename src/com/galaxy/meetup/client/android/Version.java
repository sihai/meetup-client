/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.Internal.EnumLite;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.UninitializedMessageException;


/**
 * 
 * @author sihai
 *
 */
public class Version {

	public static final class ClientVersion extends GeneratedMessageLite implements ClientVersionOrBuilder {
		public static ClientVersion getDefaultInstance() {
			return defaultInstance;
		}

		private ByteString getDeviceHardwareBytes() {
			Object obj = deviceHardware_;
			ByteString bytestring;
			if (obj instanceof String) {
				bytestring = ByteString.copyFromUtf8((String) obj);
				deviceHardware_ = bytestring;
			} else {
				bytestring = (ByteString) obj;
			}
			return bytestring;
		}

		private ByteString getDeviceOsBytes() {
			Object obj = deviceOs_;
			ByteString bytestring;
			if (obj instanceof String) {
				bytestring = ByteString.copyFromUtf8((String) obj);
				deviceOs_ = bytestring;
			} else {
				bytestring = (ByteString) obj;
			}
			return bytestring;
		}

		public static Builder newBuilder() {
			return null;
		}

		public static Builder newBuilder(ClientVersion clientversion) {
			return null;
		}

		public final App getApp() {
			return app_;
		}

		public final BuildType getBuildType() {
			return buildType_;
		}

		public final int getDataVersion() {
			return dataVersion_;
		}

		public final String getDeviceHardware() {
			Object obj = deviceHardware_;
			String s1;
			if (obj instanceof String) {
				s1 = (String) obj;
			} else {
				ByteString bytestring = (ByteString) obj;
				String s = bytestring.toStringUtf8();
				if (Internal.isValidUtf8(bytestring))
					deviceHardware_ = s;
				s1 = s;
			}
			return s1;
		}

		public final String getDeviceOs() {
			Object obj = deviceOs_;
			String s1;
			if (obj instanceof String) {
				s1 = (String) obj;
			} else {
				ByteString bytestring = (ByteString) obj;
				String s = bytestring.toStringUtf8();
				if (Internal.isValidUtf8(bytestring))
					deviceOs_ = s;
				s1 = s;
			}
			return s1;
		}

		public final PlatformType getPlatformType() {
			return platformType_;
		}

		public final int getSerializedSize() {
			int i = memoizedSerializedSize;
			int l;
			if (i != -1) {
				l = i;
			} else {
				int j = 1 & bitField0_;
				int k = 0;
				if (j == 1)
					k = 0 + CodedOutputStream.computeEnumSize(1, app_.getNumber());
				if ((2 & bitField0_) == 2)
					k += CodedOutputStream.computeEnumSize(2, buildType_.getNumber());
				if ((4 & bitField0_) == 4)
					k += CodedOutputStream.computeEnumSize(3, platformType_.getNumber());
				if ((8 & bitField0_) == 8)
					k += CodedOutputStream.computeInt32Size(4, version_);
				if ((0x10 & bitField0_) == 16)
					k += CodedOutputStream.computeInt32Size(5, dataVersion_);
				if ((0x20 & bitField0_) == 32)
					k += CodedOutputStream.computeBytesSize(6, getDeviceOsBytes());
				if ((0x40 & bitField0_) == 64)
					k += CodedOutputStream.computeBytesSize(7, getDeviceHardwareBytes());
				memoizedSerializedSize = k;
				l = k;
			}
			return l;
		}

		public final int getVersion() {
			return version_;
		}

		public final boolean hasApp() {
			boolean flag = true;
			if ((1 & bitField0_) != 1)
				flag = false;
			return flag;
		}

		public final boolean hasBuildType() {
			boolean flag;
			if ((2 & bitField0_) == 2)
				flag = true;
			else
				flag = false;
			return flag;
		}

		public final boolean hasDataVersion() {
			boolean flag;
			if ((0x10 & bitField0_) == 16)
				flag = true;
			else
				flag = false;
			return flag;
		}

		public final boolean hasDeviceHardware() {
			boolean flag;
			if ((0x40 & bitField0_) == 64)
				flag = true;
			else
				flag = false;
			return flag;
		}

		public final boolean hasDeviceOs() {
			boolean flag;
			if ((0x20 & bitField0_) == 32)
				flag = true;
			else
				flag = false;
			return flag;
		}

		public final boolean hasPlatformType() {
			boolean flag;
			if ((4 & bitField0_) == 4)
				flag = true;
			else
				flag = false;
			return flag;
		}

		public final boolean hasVersion() {
			boolean flag;
			if ((8 & bitField0_) == 8)
				flag = true;
			else
				flag = false;
			return flag;
		}

		public final boolean isInitialized() {
			boolean flag = true;
			byte byte0 = memoizedIsInitialized;
			if (byte0 != -1) {
				flag = false;
			}
			return flag;
		}

		public final com.google.protobuf.MessageLite.Builder newBuilderForType() {
			return null;
		}

		public final com.google.protobuf.MessageLite.Builder toBuilder() {
			return null;
		}

		protected final Object writeReplace() throws ObjectStreamException {
			return super.writeReplace();
		}

		public final void writeTo(CodedOutputStream codedoutputstream) throws IOException {
			getSerializedSize();
			if ((1 & bitField0_) == 1)
				codedoutputstream.writeEnum(1, app_.getNumber());
			if ((2 & bitField0_) == 2)
				codedoutputstream.writeEnum(2, buildType_.getNumber());
			if ((4 & bitField0_) == 4)
				codedoutputstream.writeEnum(3, platformType_.getNumber());
			if ((8 & bitField0_) == 8)
				codedoutputstream.writeInt32(4, version_);
			if ((0x10 & bitField0_) == 16)
				codedoutputstream.writeInt32(5, dataVersion_);
			if ((0x20 & bitField0_) == 32)
				codedoutputstream.writeBytes(6, getDeviceOsBytes());
			if ((0x40 & bitField0_) == 64)
				codedoutputstream.writeBytes(7, getDeviceHardwareBytes());
		}

		private static final ClientVersion defaultInstance;
		private static App app_;
		private int bitField0_;
		private static BuildType buildType_;
		private static int dataVersion_;
		private static Object deviceHardware_;
		private static Object deviceOs_;
		private byte memoizedIsInitialized;
		private int memoizedSerializedSize;
		private static PlatformType platformType_;
		private static int version_;

		static {
			ClientVersion clientversion = new ClientVersion();
			defaultInstance = clientversion;
			app_ = App.GOOGLE_PLUS;
			buildType_ = BuildType.DEVELOPER;
			platformType_ = PlatformType.WEB;
			version_ = 0;
			dataVersion_ = 0;
			deviceOs_ = "";
			deviceHardware_ = "";
		}

		private ClientVersion() {
			memoizedIsInitialized = -1;
			memoizedSerializedSize = -1;
		}

		private ClientVersion(Builder builder) {
			memoizedIsInitialized = -1;
			memoizedSerializedSize = -1;
		}

		ClientVersion(Builder builder, byte byte0) {
			this(builder);
		}

		@Override
		public MessageLite getDefaultInstanceForType() {
			// TODO Auto-generated method stub
			return null;
		}

		public class Builder extends com.google.protobuf.GeneratedMessageLite.Builder implements ClientVersionOrBuilder {

			public final ClientVersion build() {
				ClientVersion clientversion = buildPartial();
				if (!clientversion.isInitialized())
					throw new UninitializedMessageException(clientversion);
				else
					return clientversion;
			}

			public final ClientVersion buildPartial() {
				ClientVersion clientversion = new ClientVersion(this, (byte) 0);
				int i = bitField0_;
				int j = i & 1;
				int k = 0;
				if (j == 1)
					k = 1;
				clientversion.app_ = app_;
				if ((i & 2) == 2)
					k |= 2;
				clientversion.buildType_ = buildType_;
				if ((i & 4) == 4)
					k |= 4;
				clientversion.platformType_ = platformType_;
				if ((i & 8) == 8)
					k |= 8;
				clientversion.version_ = version_;
				if ((i & 0x10) == 16)
					k |= 0x10;
				clientversion.dataVersion_ = dataVersion_;
				if ((i & 0x20) == 32)
					k |= 0x20;
				clientversion.deviceOs_ = deviceOs_;
				if ((i & 0x40) == 64)
					k |= 0x40;
				clientversion.deviceHardware_ = deviceHardware_;
				clientversion.bitField0_ = k;
				return clientversion;
			}

			public final GeneratedMessageLite getDefaultInstanceForType() {
				return ClientVersion.getDefaultInstance();
			}

			public final boolean isInitialized() {
				return true;
			}

			public final Builder mergeFrom(ClientVersion clientversion) {
				if (clientversion != ClientVersion.getDefaultInstance()) {
					if (clientversion.hasApp())
						setApp(clientversion.getApp());
					if (clientversion.hasBuildType())
						setBuildType(clientversion.getBuildType());
					if (clientversion.hasPlatformType())
						setPlatformType(clientversion.getPlatformType());
					if (clientversion.hasVersion())
						setVersion(clientversion.getVersion());
					if (clientversion.hasDataVersion())
						setDataVersion(clientversion.getDataVersion());
					if (clientversion.hasDeviceOs())
						setDeviceOs(clientversion.getDeviceOs());
					if (clientversion.hasDeviceHardware())
						setDeviceHardware(clientversion.getDeviceHardware());
				}
				return this;
			}

			public final Builder setApp(App app) {
				if (app == null) {
					throw new NullPointerException();
				} else {
					bitField0_ = 1 | bitField0_;
					app_ = app;
					return this;
				}
			}

			public final Builder setBuildType(BuildType buildtype) {
				if (buildtype == null) {
					throw new NullPointerException();
				} else {
					bitField0_ = 2 | bitField0_;
					buildType_ = buildtype;
					return this;
				}
			}

			public final Builder setDataVersion(int i) {
				bitField0_ = 0x10 | bitField0_;
				dataVersion_ = i;
				return this;
			}

			public final Builder setDeviceHardware(String s) {
				if (s == null) {
					throw new NullPointerException();
				} else {
					bitField0_ = 0x40 | bitField0_;
					deviceHardware_ = s;
					return this;
				}
			}

			public final Builder setDeviceOs(String s) {
				if (s == null) {
					throw new NullPointerException();
				} else {
					bitField0_ = 0x20 | bitField0_;
					deviceOs_ = s;
					return this;
				}
			}

			public final Builder setPlatformType(PlatformType platformtype) {
				if (platformtype == null) {
					throw new NullPointerException();
				} else {
					bitField0_ = 4 | bitField0_;
					platformType_ = platformtype;
					return this;
				}
			}

			public final Builder setVersion(int i) {
				bitField0_ = 8 | bitField0_;
				version_ = i;
				return this;
			}

			private App app_;
			private int bitField0_;
			private BuildType buildType_;
			private int dataVersion_;
			private Object deviceHardware_;
			private Object deviceOs_;
			private PlatformType platformType_;
			private int version_;

			private Builder() {
				app_ = App.GOOGLE_PLUS;
				buildType_ = BuildType.DEVELOPER;
				platformType_ = PlatformType.WEB;
				deviceOs_ = "";
				deviceHardware_ = "";
			}

			@Override
			public com.google.protobuf.GeneratedMessageLite.Builder mergeFrom(GeneratedMessageLite arg0) {
				return null;
			}

			@Override
			public com.google.protobuf.AbstractMessageLite.Builder mergeFrom(CodedInputStream arg0,
					ExtensionRegistryLite arg1) throws IOException {
				return null;
			}

		}

		public enum App implements EnumLite {
			GOOGLE_PLUS(1), MESSAGING(2);

			private int number;

			private App(int number) {
				this.number = number;
			}

			public int getNumber() {
				return number;
			}

			public void setNumber(int number) {
				this.number = number;
			}

			public static final EnumLite findValueByNumber(int i) {
				for (App app : App.values()) {
					if (app.getNumber() == i) {
						return app;
					}
				}
				return null;
			}
		}

		public enum BuildType implements EnumLite {
			DEVELOPER(1), DOGFOOD(2), PUBLIC(3);

			private int number;

			private BuildType(int number) {
				this.number = number;
			}

			public int getNumber() {
				return number;
			}

			public void setNumber(int number) {
				this.number = number;
			}

			public static final EnumLite findValueByNumber(int i) {
				for (BuildType buildType : BuildType.values()) {
					if (buildType.getNumber() == i) {
						return buildType;
					}
				}
				return null;
			}
		}

		public enum PlatformType implements EnumLite {

			WEB(0), IOS(1), ANDROID(1);
			private int number;

			private PlatformType(int number) {
				this.number = number;
			}

			public int getNumber() {
				return number;
			}

			public void setNumber(int number) {
				this.number = number;
			}

			public static final EnumLite findValueByNumber(int i) {
				for (PlatformType platformType : PlatformType.values()) {
					if (platformType.getNumber() == i) {
						return platformType;
					}
				}
				return null;
			}
		}

	}

	public static interface ClientVersionOrBuilder extends MessageLiteOrBuilder {
	}

	private Version() {
	}
}
