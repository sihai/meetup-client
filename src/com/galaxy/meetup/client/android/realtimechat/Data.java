/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.realtimechat;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Collections;
import java.util.List;

import com.galaxy.meetup.client.android.Version;
import com.galaxy.meetup.client.android.Version.ClientVersion;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.LazyStringArrayList;
import com.google.protobuf.LazyStringList;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.UninitializedMessageException;
import com.google.protobuf.UnmodifiableLazyStringList;

/**
 * 
 * @author sihai
 *
 */
public class Data {

	// ==================================================================================================================
		// Inner class
		// ==================================================================================================================

		public static interface TimeRangeOrBuilder extends MessageLiteOrBuilder {

			public abstract long getEnd();

			public abstract long getStart();

			public abstract boolean hasEnd();

			public abstract boolean hasStart();
		}

		public static final class TimeRange extends GeneratedMessageLite implements TimeRangeOrBuilder {

			public static TimeRange getDefaultInstance() {
				return defaultInstance;
			}

			public static Builder newBuilder() {
				return null;
			}

			public static Builder newBuilder(TimeRange timerange) {
				return null;
			}

			public final TimeRange getDefaultInstanceForType() {
				return defaultInstance;
			}

			public final long getEnd() {
				return end_;
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
						k = 0 + CodedOutputStream.computeInt64Size(1, start_);
					if ((2 & bitField0_) == 2)
						k += CodedOutputStream.computeInt64Size(2, end_);
					memoizedSerializedSize = k;
					l = k;
				}
				return l;
			}

			public final long getStart() {
				return start_;
			}

			public final boolean hasEnd() {
				boolean flag;
				if ((2 & bitField0_) == 2)
					flag = true;
				else
					flag = false;
				return flag;
			}

			public final boolean hasStart() {
				boolean flag = true;
				if ((1 & bitField0_) != 1)
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
					codedoutputstream.writeInt64(1, start_);
				if ((2 & bitField0_) == 2)
					codedoutputstream.writeInt64(2, end_);
			}

			private static final TimeRange defaultInstance;
			private int bitField0_;
			private static long end_;
			private byte memoizedIsInitialized;
			private int memoizedSerializedSize;
			private static long start_;

			static {
				TimeRange timerange = new TimeRange();
				defaultInstance = timerange;
				start_ = 0L;
				end_ = 0L;
			}

			private TimeRange() {
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			private TimeRange(Builder builder) {
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			TimeRange(Builder builder, byte byte0) {
				this(builder);
			}

			public static final class Builder extends com.google.protobuf.GeneratedMessageLite.Builder implements
					TimeRangeOrBuilder {

				public Builder clone() {
					return (new Builder()).mergeFrom(buildPartial());
				}

				public final TimeRange build() {
					TimeRange timerange = buildPartial();
					if (!isInitialized())
						throw new UninitializedMessageException(timerange);
					else
						return timerange;
				}

				public final TimeRange buildPartial() {
					TimeRange timerange = new TimeRange(this, (byte) 0);
					int i = bitField0_;
					int j = i & 1;
					int k = 0;
					if (j == 1)
						k = 1;
					start_ = start_;
					if ((i & 2) == 2)
						k |= 2;
					end_ = end_;
					bitField0_ = k;
					return timerange;
				}

				public final Builder clear() {
					super.clear();
					start_ = 0L;
					bitField0_ = -2 & bitField0_;
					end_ = 0L;
					bitField0_ = -3 & bitField0_;
					return this;
				}

				public final Builder clearEnd() {
					bitField0_ = -3 & bitField0_;
					end_ = 0L;
					return this;
				}

				public final Builder clearStart() {
					bitField0_ = -2 & bitField0_;
					start_ = 0L;
					return this;
				}

				public final TimeRange getDefaultInstanceForType() {
					return getDefaultInstance();
				}

				public final long getEnd() {
					return end_;
				}

				public final long getStart() {
					return start_;
				}

				public final boolean hasEnd() {
					boolean flag;
					if ((2 & bitField0_) == 2)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean hasStart() {
					boolean flag = true;
					if ((1 & bitField0_) != 1)
						flag = false;
					return flag;
				}

				public final boolean isInitialized() {
					return true;
				}

				public final Builder mergeFrom(TimeRange timerange) {
					if (timerange != getDefaultInstance()) {

						if (hasStart())
							setStart(getStart());
						if (hasEnd())
							setEnd(getEnd());
					}
					return this;
				}

				public final Builder setEnd(long l) {
					bitField0_ = 2 | bitField0_;
					end_ = l;
					return this;
				}

				public final Builder setStart(long l) {
					bitField0_ = 1 | bitField0_;
					start_ = l;
					return this;
				}

				private int bitField0_;
				private long end_;
				private long start_;

				private Builder() {
				}

				@Override
				public com.google.protobuf.GeneratedMessageLite.Builder mergeFrom(GeneratedMessageLite arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public com.google.protobuf.AbstractMessageLite.Builder mergeFrom(CodedInputStream arg0,
						ExtensionRegistryLite arg1) throws IOException {
					// TODO Auto-generated method stub
					return null;
				}
			}
		}

		public static interface ConversationAttributesOrBuilder extends MessageLiteOrBuilder {

			public abstract String getConversationId();

			public abstract String getConversationName();

			public abstract boolean hasConversationId();

			public abstract boolean hasConversationName();
		}

		public static final class ConversationAttributes extends GeneratedMessageLite implements
				ConversationAttributesOrBuilder {

			private ByteString getConversationIdBytes() {
				Object obj = conversationId_;
				ByteString bytestring;
				if (obj instanceof String) {
					bytestring = ByteString.copyFromUtf8((String) obj);
					conversationId_ = bytestring;
				} else {
					bytestring = (ByteString) obj;
				}
				return bytestring;
			}

			private ByteString getConversationNameBytes() {
				Object obj = conversationName_;
				ByteString bytestring;
				if (obj instanceof String) {
					bytestring = ByteString.copyFromUtf8((String) obj);
					conversationName_ = bytestring;
				} else {
					bytestring = (ByteString) obj;
				}
				return bytestring;
			}

			public static ConversationAttributes getDefaultInstance() {
				return defaultInstance;
			}

			public static Builder newBuilder() {
				return new Builder();
			}

			public final String getConversationId() {
				Object obj = conversationId_;
				String s1;
				if (obj instanceof String) {
					s1 = (String) obj;
				} else {
					ByteString bytestring = (ByteString) obj;
					String s = bytestring.toStringUtf8();
					if (Internal.isValidUtf8(bytestring))
						conversationId_ = s;
					s1 = s;
				}
				return s1;
			}

			public final String getConversationName() {
				Object obj = conversationName_;
				String s1;
				if (obj instanceof String) {
					s1 = (String) obj;
				} else {
					ByteString bytestring = (ByteString) obj;
					String s = bytestring.toStringUtf8();
					if (Internal.isValidUtf8(bytestring))
						conversationName_ = s;
					s1 = s;
				}
				return s1;
			}

			public final ConversationAttributes getDefaultInstanceForType() {
				return defaultInstance;
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
						k = 0 + CodedOutputStream.computeBytesSize(1, getConversationNameBytes());
					if ((2 & bitField0_) == 2)
						k += CodedOutputStream.computeBytesSize(2, getConversationIdBytes());
					memoizedSerializedSize = k;
					l = k;
				}
				return l;
			}

			public final boolean hasConversationId() {
				boolean flag;
				if ((2 & bitField0_) == 2)
					flag = true;
				else
					flag = false;
				return flag;
			}

			public final boolean hasConversationName() {
				boolean flag = true;
				if ((1 & bitField0_) != 1)
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
					codedoutputstream.writeBytes(1, getConversationNameBytes());
				if ((2 & bitField0_) == 2)
					codedoutputstream.writeBytes(2, getConversationIdBytes());
			}

			private static final ConversationAttributes defaultInstance;
			private int bitField0_;
			private Object conversationId_;
			private Object conversationName_;
			private byte memoizedIsInitialized;
			private int memoizedSerializedSize;

			static {
				ConversationAttributes conversationattributes = new ConversationAttributes();
				defaultInstance = conversationattributes;
				conversationattributes.conversationName_ = "";
				conversationattributes.conversationId_ = "";
			}

			private ConversationAttributes() {
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			private ConversationAttributes(Builder builder) {
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			ConversationAttributes(Builder builder, byte byte0) {
				this(builder);
			}

			public static final class Builder extends com.google.protobuf.GeneratedMessageLite.Builder implements
					ConversationAttributesOrBuilder {

				public Builder clone() {
					return (new ConversationAttributes.Builder()).mergeFrom(buildPartial());
				}

				public final ConversationAttributes build() {
					ConversationAttributes conversationattributes = buildPartial();
					if (!conversationattributes.isInitialized())
						throw new UninitializedMessageException(conversationattributes);
					else
						return conversationattributes;
				}

				public final ConversationAttributes buildPartial() {
					ConversationAttributes conversationattributes = new ConversationAttributes(this, (byte) 0);
					int i = bitField0_;
					int j = i & 1;
					int k = 0;
					if (j == 1)
						k = 1;
					conversationattributes.conversationName_ = conversationName_;
					if ((i & 2) == 2)
						k |= 2;
					conversationattributes.conversationId_ = conversationId_;
					conversationattributes.bitField0_ = k;
					return conversationattributes;
				}

				public final ConversationAttributes.Builder clear() {
					super.clear();
					conversationName_ = "";
					bitField0_ = -2 & bitField0_;
					conversationId_ = "";
					bitField0_ = -3 & bitField0_;
					return this;
				}

				public final ConversationAttributes.Builder clearConversationId() {
					bitField0_ = -3 & bitField0_;
					conversationId_ = ConversationAttributes.getDefaultInstance().getConversationId();
					return this;
				}

				public final ConversationAttributes.Builder clearConversationName() {
					bitField0_ = -2 & bitField0_;
					conversationName_ = ConversationAttributes.getDefaultInstance().getConversationName();
					return this;
				}

				public final String getConversationId() {
					Object obj = conversationId_;
					String s;
					if (!(obj instanceof String)) {
						s = ((ByteString) obj).toStringUtf8();
						conversationId_ = s;
					} else {
						s = (String) obj;
					}
					return s;
				}

				public final String getConversationName() {
					Object obj = conversationName_;
					String s;
					if (!(obj instanceof String)) {
						s = ((ByteString) obj).toStringUtf8();
						conversationName_ = s;
					} else {
						s = (String) obj;
					}
					return s;
				}

				public final ConversationAttributes getDefaultInstanceForType() {
					return ConversationAttributes.getDefaultInstance();
				}

				public final boolean hasConversationId() {
					boolean flag;
					if ((2 & bitField0_) == 2)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean hasConversationName() {
					boolean flag = true;
					if ((1 & bitField0_) != 1)
						flag = false;
					return flag;
				}

				public final boolean isInitialized() {
					return true;
				}

				public final ConversationAttributes.Builder mergeFrom(ConversationAttributes conversationattributes) {
					if (conversationattributes != ConversationAttributes.getDefaultInstance()) {
						if (conversationattributes.hasConversationName())
							setConversationName(conversationattributes.getConversationName());
						if (conversationattributes.hasConversationId())
							setConversationId(conversationattributes.getConversationId());
					}
					return this;
				}

				public final Builder setConversationId(String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						bitField0_ = 2 | bitField0_;
						conversationId_ = s;
						return this;
					}
				}

				public final Builder setConversationName(String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						bitField0_ = 1 | bitField0_;
						conversationName_ = s;
						return this;
					}
				}

				private int bitField0_;
				private Object conversationId_;
				private Object conversationName_;

				private Builder() {
					conversationName_ = "";
					conversationId_ = "";
				}

				@Override
				public com.google.protobuf.GeneratedMessageLite.Builder mergeFrom(GeneratedMessageLite arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public com.google.protobuf.AbstractMessageLite.Builder mergeFrom(CodedInputStream arg0,
						ExtensionRegistryLite arg1) throws IOException {
					// TODO Auto-generated method stub
					return null;
				}
			}

		}

		public static interface ParticipantAttributesOrBuilder extends MessageLiteOrBuilder {

			public abstract String getFirstName();

			public abstract String getFullName();

			public abstract String getParticipantId();

			public abstract boolean hasFirstName();

			public abstract boolean hasFullName();

			public abstract boolean hasParticipantId();
		}

		public static final class ParticipantAttributes extends GeneratedMessageLite implements
				ParticipantAttributesOrBuilder {

			public static ParticipantAttributes getDefaultInstance() {
				return defaultInstance;
			}

			private ByteString getFirstNameBytes() {
				Object obj = firstName_;
				ByteString bytestring;
				if (obj instanceof String) {
					bytestring = ByteString.copyFromUtf8((String) obj);
					firstName_ = bytestring;
				} else {
					bytestring = (ByteString) obj;
				}
				return bytestring;
			}

			private ByteString getFullNameBytes() {
				Object obj = fullName_;
				ByteString bytestring;
				if (obj instanceof String) {
					bytestring = ByteString.copyFromUtf8((String) obj);
					fullName_ = bytestring;
				} else {
					bytestring = (ByteString) obj;
				}
				return bytestring;
			}

			private ByteString getParticipantIdBytes() {
				Object obj = participantId_;
				ByteString bytestring;
				if (obj instanceof String) {
					bytestring = ByteString.copyFromUtf8((String) obj);
					participantId_ = bytestring;
				} else {
					bytestring = (ByteString) obj;
				}
				return bytestring;
			}

			public static Builder newBuilder() {
				return null;
			}

			public final ParticipantAttributes getDefaultInstanceForType() {
				return defaultInstance;
			}

			public final String getFirstName() {
				Object obj = firstName_;
				String s1;
				if (obj instanceof String) {
					s1 = (String) obj;
				} else {
					ByteString bytestring = (ByteString) obj;
					String s = bytestring.toStringUtf8();
					if (Internal.isValidUtf8(bytestring))
						firstName_ = s;
					s1 = s;
				}
				return s1;
			}

			public final String getFullName() {
				Object obj = fullName_;
				String s1;
				if (obj instanceof String) {
					s1 = (String) obj;
				} else {
					ByteString bytestring = (ByteString) obj;
					String s = bytestring.toStringUtf8();
					if (Internal.isValidUtf8(bytestring))
						fullName_ = s;
					s1 = s;
				}
				return s1;
			}

			public final String getParticipantId() {
				Object obj = participantId_;
				String s1;
				if (obj instanceof String) {
					s1 = (String) obj;
				} else {
					ByteString bytestring = (ByteString) obj;
					String s = bytestring.toStringUtf8();
					if (Internal.isValidUtf8(bytestring))
						participantId_ = s;
					s1 = s;
				}
				return s1;
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
						k = 0 + CodedOutputStream.computeBytesSize(1, getParticipantIdBytes());
					if ((2 & bitField0_) == 2)
						k += CodedOutputStream.computeBytesSize(2, getFullNameBytes());
					if ((4 & bitField0_) == 4)
						k += CodedOutputStream.computeBytesSize(3, getFirstNameBytes());
					memoizedSerializedSize = k;
					l = k;
				}
				return l;
			}

			public final boolean hasFirstName() {
				boolean flag;
				if ((4 & bitField0_) == 4)
					flag = true;
				else
					flag = false;
				return flag;
			}

			public final boolean hasFullName() {
				boolean flag;
				if ((2 & bitField0_) == 2)
					flag = true;
				else
					flag = false;
				return flag;
			}

			public final boolean hasParticipantId() {
				boolean flag = true;
				if ((1 & bitField0_) != 1)
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
					codedoutputstream.writeBytes(1, getParticipantIdBytes());
				if ((2 & bitField0_) == 2)
					codedoutputstream.writeBytes(2, getFullNameBytes());
				if ((4 & bitField0_) == 4)
					codedoutputstream.writeBytes(3, getFirstNameBytes());
			}

			private static final ParticipantAttributes defaultInstance;
			private int bitField0_;
			private Object firstName_;
			private Object fullName_;
			private byte memoizedIsInitialized;
			private int memoizedSerializedSize;
			private Object participantId_;

			static {
				ParticipantAttributes participantattributes = new ParticipantAttributes();
				defaultInstance = participantattributes;
				participantattributes.participantId_ = "";
				participantattributes.fullName_ = "";
				participantattributes.firstName_ = "";
			}

			private ParticipantAttributes() {
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			private ParticipantAttributes(Builder builder) {
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			ParticipantAttributes(Builder builder, byte byte0) {
				this(builder);
			}

			public static final class Builder extends com.google.protobuf.GeneratedMessageLite.Builder implements
					ParticipantAttributesOrBuilder {

				public ParticipantAttributes.Builder clone() {
					return (new ParticipantAttributes.Builder()).mergeFrom(buildPartial());
				}

				public final ParticipantAttributes build() {
					ParticipantAttributes participantattributes = buildPartial();
					if (!participantattributes.isInitialized())
						throw new UninitializedMessageException(participantattributes);
					else
						return participantattributes;
				}

				public final ParticipantAttributes buildPartial() {
					ParticipantAttributes participantattributes = new ParticipantAttributes(this, (byte) 0);
					int i = bitField0_;
					int j = i & 1;
					int k = 0;
					if (j == 1)
						k = 1;
					participantattributes.participantId_ = participantId_;
					if ((i & 2) == 2)
						k |= 2;
					participantattributes.fullName_ = fullName_;
					if ((i & 4) == 4)
						k |= 4;
					participantattributes.firstName_ = firstName_;
					participantattributes.bitField0_ = k;
					return participantattributes;
				}

				public final ParticipantAttributes.Builder clear() {
					super.clear();
					participantId_ = "";
					bitField0_ = -2 & bitField0_;
					fullName_ = "";
					bitField0_ = -3 & bitField0_;
					firstName_ = "";
					bitField0_ = -5 & bitField0_;
					return this;
				}

				public final ParticipantAttributes.Builder clearFirstName() {
					bitField0_ = -5 & bitField0_;
					firstName_ = ParticipantAttributes.getDefaultInstance().getFirstName();
					return this;
				}

				public final ParticipantAttributes.Builder clearFullName() {
					bitField0_ = -3 & bitField0_;
					fullName_ = ParticipantAttributes.getDefaultInstance().getFullName();
					return this;
				}

				public final ParticipantAttributes.Builder clearParticipantId() {
					bitField0_ = -2 & bitField0_;
					participantId_ = ParticipantAttributes.getDefaultInstance().getParticipantId();
					return this;
				}

				public final ParticipantAttributes getDefaultInstanceForType() {
					return ParticipantAttributes.getDefaultInstance();
				}

				public final String getFirstName() {
					Object obj = firstName_;
					String s;
					if (!(obj instanceof String)) {
						s = ((ByteString) obj).toStringUtf8();
						firstName_ = s;
					} else {
						s = (String) obj;
					}
					return s;
				}

				public final String getFullName() {
					Object obj = fullName_;
					String s;
					if (!(obj instanceof String)) {
						s = ((ByteString) obj).toStringUtf8();
						fullName_ = s;
					} else {
						s = (String) obj;
					}
					return s;
				}

				public final String getParticipantId() {
					Object obj = participantId_;
					String s;
					if (!(obj instanceof String)) {
						s = ((ByteString) obj).toStringUtf8();
						participantId_ = s;
					} else {
						s = (String) obj;
					}
					return s;
				}

				public final boolean hasFirstName() {
					boolean flag;
					if ((4 & bitField0_) == 4)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean hasFullName() {
					boolean flag;
					if ((2 & bitField0_) == 2)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean hasParticipantId() {
					boolean flag = true;
					if ((1 & bitField0_) != 1)
						flag = false;
					return flag;
				}

				public final boolean isInitialized() {
					return true;
				}

				public final ParticipantAttributes.Builder mergeFrom(ParticipantAttributes participantattributes) {
					if (participantattributes != ParticipantAttributes.getDefaultInstance()) {
						if (participantattributes.hasParticipantId())
							setParticipantId(participantattributes.getParticipantId());
						if (participantattributes.hasFullName())
							setFullName(participantattributes.getFullName());
						if (participantattributes.hasFirstName())
							setFirstName(participantattributes.getFirstName());
					}
					return this;
				}

				public final ParticipantAttributes.Builder setFirstName(String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						bitField0_ = 4 | bitField0_;
						firstName_ = s;
						return this;
					}
				}

				public final ParticipantAttributes.Builder setFullName(String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						bitField0_ = 2 | bitField0_;
						fullName_ = s;
						return this;
					}
				}

				public final Builder setParticipantId(String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						bitField0_ = 1 | bitField0_;
						participantId_ = s;
						return this;
					}
				}

				private int bitField0_;
				private Object firstName_;
				private Object fullName_;
				private Object participantId_;

				private Builder() {
					participantId_ = "";
					fullName_ = "";
					firstName_ = "";
				}

				@Override
				public com.google.protobuf.GeneratedMessageLite.Builder mergeFrom(GeneratedMessageLite arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public com.google.protobuf.AbstractMessageLite.Builder mergeFrom(CodedInputStream arg0,
						ExtensionRegistryLite arg1) throws IOException {
					// TODO Auto-generated method stub
					return null;
				}
			}

		}

		public static interface LocationOrBuilder extends MessageLiteOrBuilder {

			public abstract double getAccuracy();

			public abstract double getLatitude();

			public abstract double getLongitude();

			public abstract String getName();

			public abstract boolean hasAccuracy();

			public abstract boolean hasLatitude();

			public abstract boolean hasLongitude();

			public abstract boolean hasName();
		}

		public static final class Location extends GeneratedMessageLite implements LocationOrBuilder {

			public static Location getDefaultInstance() {
				return defaultInstance;
			}

			private ByteString getNameBytes() {
				Object obj = name_;
				ByteString bytestring;
				if (obj instanceof String) {
					bytestring = ByteString.copyFromUtf8((String) obj);
					name_ = bytestring;
				} else {
					bytestring = (ByteString) obj;
				}
				return bytestring;
			}

			public static Builder newBuilder() {
				return new Builder();
			}

			public static Builder newBuilder(Location location) {
				return new Builder().mergeFrom(location);
			}

			public final double getAccuracy() {
				return accuracy_;
			}

			public final double getLatitude() {
				return latitude_;
			}

			public final double getLongitude() {
				return longitude_;
			}

			public final String getName() {
				Object obj = name_;
				String s1;
				if (obj instanceof String) {
					s1 = (String) obj;
				} else {
					ByteString bytestring = (ByteString) obj;
					String s = bytestring.toStringUtf8();
					if (Internal.isValidUtf8(bytestring))
						name_ = s;
					s1 = s;
				}
				return s1;
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
						k = 0 + CodedOutputStream.computeDoubleSize(1, latitude_);
					if ((2 & bitField0_) == 2)
						k += CodedOutputStream.computeDoubleSize(2, longitude_);
					if ((4 & bitField0_) == 4)
						k += CodedOutputStream.computeDoubleSize(3, accuracy_);
					if ((8 & bitField0_) == 8)
						k += CodedOutputStream.computeBytesSize(4, getNameBytes());
					memoizedSerializedSize = k;
					l = k;
				}
				return l;
			}

			public final boolean hasAccuracy() {
				boolean flag;
				if ((4 & bitField0_) == 4)
					flag = true;
				else
					flag = false;
				return flag;
			}

			public final boolean hasLatitude() {
				boolean flag = true;
				if ((1 & bitField0_) != 1)
					flag = false;
				return flag;
			}

			public final boolean hasLongitude() {
				boolean flag;
				if ((2 & bitField0_) == 2)
					flag = true;
				else
					flag = false;
				return flag;
			}

			public final boolean hasName() {
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
					codedoutputstream.writeDouble(1, latitude_);
				if ((2 & bitField0_) == 2)
					codedoutputstream.writeDouble(2, longitude_);
				if ((4 & bitField0_) == 4)
					codedoutputstream.writeDouble(3, accuracy_);
				if ((8 & bitField0_) == 8)
					codedoutputstream.writeBytes(4, getNameBytes());
			}

			private static final Location defaultInstance;
			private double accuracy_;
			private int bitField0_;
			private double latitude_;
			private double longitude_;
			private byte memoizedIsInitialized;
			private int memoizedSerializedSize;
			private Object name_;

			private Location() {
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			private Location(Builder builder) {
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			Location(Builder builder, byte byte0) {
				this(builder);
			}

			static {
				Location location = new Location();
				defaultInstance = location;
				location.latitude_ = 0.0D;
				location.longitude_ = 0.0D;
				location.accuracy_ = 0.0D;
				location.name_ = "";
			}

			public static interface LocationOrBuilder extends MessageLiteOrBuilder {

				public abstract double getAccuracy();

				public abstract double getLatitude();

				public abstract double getLongitude();

				public abstract String getName();

				public abstract boolean hasAccuracy();

				public abstract boolean hasLatitude();

				public abstract boolean hasLongitude();

				public abstract boolean hasName();
			}

			public static final class Builder extends com.google.protobuf.GeneratedMessageLite.Builder implements
					LocationOrBuilder {

				public Builder clone() {
					return (new Location.Builder()).mergeFrom(buildPartial());
				}

				public final Location build() {
					Location location = buildPartial();
					if (!location.isInitialized())
						throw new UninitializedMessageException(location);
					else
						return location;
				}

				public final Location buildPartial() {
					Location location = new Location();
					int i = bitField0_;
					int j = i & 1;
					int k = 0;
					if (j == 1)
						k = 1;
					location.latitude_ = latitude_;
					if ((i & 2) == 2)
						k |= 2;
					location.longitude_ = longitude_;
					if ((i & 4) == 4)
						k |= 4;
					location.accuracy_ = accuracy_;
					if ((i & 8) == 8)
						k |= 8;
					location.name_ = name_;
					location.bitField0_ = k;
					return location;
				}

				public final Location.Builder clear() {
					super.clear();
					latitude_ = 0.0D;
					bitField0_ = -2 & bitField0_;
					longitude_ = 0.0D;
					bitField0_ = -3 & bitField0_;
					accuracy_ = 0.0D;
					bitField0_ = -5 & bitField0_;
					name_ = "";
					bitField0_ = -9 & bitField0_;
					return this;
				}

				public final Location.Builder clearAccuracy() {
					bitField0_ = -5 & bitField0_;
					accuracy_ = 0.0D;
					return this;
				}

				public final Location.Builder clearLatitude() {
					bitField0_ = -2 & bitField0_;
					latitude_ = 0.0D;
					return this;
				}

				public final Location.Builder clearLongitude() {
					bitField0_ = -3 & bitField0_;
					longitude_ = 0.0D;
					return this;
				}

				public final Location.Builder clearName() {
					bitField0_ = -9 & bitField0_;
					name_ = Location.getDefaultInstance().getName();
					return this;
				}

				public final double getAccuracy() {
					return accuracy_;
				}

				public final Location getDefaultInstanceForType() {
					return Location.getDefaultInstance();
				}

				public final double getLatitude() {
					return latitude_;
				}

				public final double getLongitude() {
					return longitude_;
				}

				public final String getName() {
					Object obj = name_;
					String s;
					if (!(obj instanceof String)) {
						s = ((ByteString) obj).toStringUtf8();
						name_ = s;
					} else {
						s = (String) obj;
					}
					return s;
				}

				public final boolean hasAccuracy() {
					boolean flag;
					if ((4 & bitField0_) == 4)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean hasLatitude() {
					boolean flag = true;
					if ((1 & bitField0_) != 1)
						flag = false;
					return flag;
				}

				public final boolean hasLongitude() {
					boolean flag;
					if ((2 & bitField0_) == 2)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean hasName() {
					boolean flag;
					if ((8 & bitField0_) == 8)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean isInitialized() {
					return true;
				}

				public final Builder mergeFrom(Location location) {
					if (location != Location.getDefaultInstance()) {
						if (location.hasLatitude())
							setLatitude(location.getLatitude());
						if (location.hasLongitude())
							setLongitude(location.getLongitude());
						if (location.hasAccuracy())
							setAccuracy(location.getAccuracy());
						if (location.hasName())
							setName(location.getName());
					}
					return this;
				}

				public final Builder setAccuracy(double d) {
					bitField0_ = 4 | bitField0_;
					accuracy_ = d;
					return this;
				}

				public final Builder setLatitude(double d) {
					bitField0_ = 1 | bitField0_;
					latitude_ = d;
					return this;
				}

				public final Builder setLongitude(double d) {
					bitField0_ = 2 | bitField0_;
					longitude_ = d;
					return this;
				}

				public final Location.Builder setName(String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						bitField0_ = 8 | bitField0_;
						name_ = s;
						return this;
					}
				}

				private double accuracy_;
				private int bitField0_;
				private double latitude_;
				private double longitude_;
				private Object name_;

				private Builder() {
					name_ = "";
				}

				@Override
				public com.google.protobuf.GeneratedMessageLite.Builder mergeFrom(GeneratedMessageLite arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public com.google.protobuf.AbstractMessageLite.Builder mergeFrom(CodedInputStream arg0,
						ExtensionRegistryLite arg1) throws IOException {
					// TODO Auto-generated method stub
					return null;
				}
			}

			@Override
			public MessageLite getDefaultInstanceForType() {
				// TODO Auto-generated method stub
				return null;
			}

		}

		public static interface KeyValueOrBuilder extends MessageLiteOrBuilder {

			public abstract String getKey();

			public abstract String getValue();

			public abstract boolean hasKey();

			public abstract boolean hasValue();
		}

		public static final class KeyValue extends GeneratedMessageLite implements KeyValueOrBuilder {

			public static KeyValue getDefaultInstance() {
				return defaultInstance;
			}

			private ByteString getKeyBytes() {
				Object obj = key_;
				ByteString bytestring;
				if (obj instanceof String) {
					bytestring = ByteString.copyFromUtf8((String) obj);
					key_ = bytestring;
				} else {
					bytestring = (ByteString) obj;
				}
				return bytestring;
			}

			private ByteString getValueBytes() {
				Object obj = value_;
				ByteString bytestring;
				if (obj instanceof String) {
					bytestring = ByteString.copyFromUtf8((String) obj);
					value_ = bytestring;
				} else {
					bytestring = (ByteString) obj;
				}
				return bytestring;
			}

			public static Builder newBuilder() {
				return null;
			}

			public final KeyValue getDefaultInstanceForType() {
				return defaultInstance;
			}

			public final String getKey() {
				Object obj = key_;
				String s1;
				if (obj instanceof String) {
					s1 = (String) obj;
				} else {
					ByteString bytestring = (ByteString) obj;
					String s = bytestring.toStringUtf8();
					if (Internal.isValidUtf8(bytestring))
						key_ = s;
					s1 = s;
				}
				return s1;
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
						k = 0 + CodedOutputStream.computeBytesSize(1, getKeyBytes());
					if ((2 & bitField0_) == 2)
						k += CodedOutputStream.computeBytesSize(2, getValueBytes());
					memoizedSerializedSize = k;
					l = k;
				}
				return l;
			}

			public final String getValue() {
				Object obj = value_;
				String s1;
				if (obj instanceof String) {
					s1 = (String) obj;
				} else {
					ByteString bytestring = (ByteString) obj;
					String s = bytestring.toStringUtf8();
					if (Internal.isValidUtf8(bytestring))
						value_ = s;
					s1 = s;
				}
				return s1;
			}

			public final boolean hasKey() {
				boolean flag = true;
				if ((1 & bitField0_) != 1)
					flag = false;
				return flag;
			}

			public final boolean hasValue() {
				boolean flag;
				if ((2 & bitField0_) == 2)
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
					codedoutputstream.writeBytes(1, getKeyBytes());
				if ((2 & bitField0_) == 2)
					codedoutputstream.writeBytes(2, getValueBytes());
			}

			private static final KeyValue defaultInstance;
			private int bitField0_;
			private Object key_;
			private byte memoizedIsInitialized;
			private int memoizedSerializedSize;
			private Object value_;

			static {
				KeyValue keyvalue = new KeyValue();
				defaultInstance = keyvalue;
				keyvalue.key_ = "";
				keyvalue.value_ = "";
			}

			private KeyValue() {
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			private KeyValue(Builder builder) {
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			KeyValue(Builder builder, byte byte0) {
				this(builder);
			}

		}

		public static interface ContentOrBuilder extends MessageLiteOrBuilder {

			public abstract String getLinkUrl();

			public abstract Location getLocation();

			public abstract PhotoMetadata getPhotoMetadata();

			public abstract String getPhotoUrl();

			public abstract String getText();

			public abstract boolean hasLinkUrl();

			public abstract boolean hasLocation();

			public abstract boolean hasPhotoMetadata();

			public abstract boolean hasPhotoUrl();

			public abstract boolean hasText();
		}

		public static interface PhotoMetadataOrBuilder extends MessageLiteOrBuilder {

			public abstract String getUrl();

			public abstract boolean hasUrl();
		}

		public static final class PhotoMetadata extends GeneratedMessageLite implements PhotoMetadataOrBuilder {

			public static PhotoMetadata getDefaultInstance() {
				return defaultInstance;
			}

			public static Builder newBuilder() {
				return null;
			}

			public static Builder newBuilder(PhotoMetadata photoMetadata) {
				return null;
			}

			private ByteString getUrlBytes() {
				Object obj = url_;
				ByteString bytestring;
				if (obj instanceof String) {
					bytestring = ByteString.copyFromUtf8((String) obj);
					url_ = bytestring;
				} else {
					bytestring = (ByteString) obj;
				}
				return bytestring;
			}

			public final PhotoMetadata getDefaultInstanceForType() {
				return defaultInstance;
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
						k = 0 + CodedOutputStream.computeBytesSize(1, getUrlBytes());
					memoizedSerializedSize = k;
					l = k;
				}
				return l;
			}

			public final String getUrl() {
				Object obj = url_;
				String s1;
				if (obj instanceof String) {
					s1 = (String) obj;
				} else {
					ByteString bytestring = (ByteString) obj;
					String s = bytestring.toStringUtf8();
					if (Internal.isValidUtf8(bytestring))
						url_ = s;
					s1 = s;
				}
				return s1;
			}

			public final boolean hasUrl() {
				boolean flag = true;
				if ((1 & bitField0_) != 1)
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

			protected final Object writeReplace() throws ObjectStreamException {
				return super.writeReplace();
			}

			public final void writeTo(CodedOutputStream codedoutputstream) throws IOException {
				getSerializedSize();
				if ((1 & bitField0_) == 1)
					codedoutputstream.writeBytes(1, getUrlBytes());
			}

			private static final PhotoMetadata defaultInstance;
			private int bitField0_;
			private byte memoizedIsInitialized;
			private int memoizedSerializedSize;
			private static Object url_;

			static {
				PhotoMetadata photometadata = new PhotoMetadata();
				defaultInstance = photometadata;
				url_ = "";
			}

			private PhotoMetadata() {
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			private PhotoMetadata(Builder builder) {
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			PhotoMetadata(Builder builder, byte byte0) {
				this(builder);
			}

			@Override
			public com.google.protobuf.MessageLite.Builder newBuilderForType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public com.google.protobuf.MessageLite.Builder toBuilder() {
				// TODO Auto-generated method stub
				return null;
			}

			public static final class Builder extends com.google.protobuf.GeneratedMessageLite.Builder implements
					PhotoMetadataOrBuilder {

				public Builder clone() {
					return (new Builder()).mergeFrom(buildPartial());
				}

				public final PhotoMetadata buildPartial() {
					PhotoMetadata photometadata = new PhotoMetadata(this, (byte) 0);
					int i = 1 & bitField0_;
					int j = 0;
					if (i == 1)
						j = 1;
					url_ = url_;
					bitField0_ = j;
					return photometadata;
				}

				public final Builder clear() {
					url_ = "";
					bitField0_ = -2 & bitField0_;
					return this;
				}

				public final Builder clearUrl() {
					bitField0_ = -2 & bitField0_;
					url_ = getDefaultInstance().getUrl();
					return this;
				}

				public final PhotoMetadata getDefaultInstanceForType() {
					return getDefaultInstance();
				}

				public final String getUrl() {
					Object obj = url_;
					String s;
					if (!(obj instanceof String)) {
						s = ((ByteString) obj).toStringUtf8();
						url_ = s;
					} else {
						s = (String) obj;
					}
					return s;
				}

				public final boolean hasUrl() {
					boolean flag = true;
					if ((1 & bitField0_) != 1)
						flag = false;
					return flag;
				}

				public final boolean isInitialized() {
					return true;
				}

				public final Builder mergeFrom(PhotoMetadata photometadata) {
					if (photometadata != getDefaultInstance() && hasUrl())
						setUrl(getUrl());
					return this;
				}

				public final Builder setUrl(String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						bitField0_ = 1 | bitField0_;
						url_ = s;
						return this;
					}
				}

				private int bitField0_;
				private Object url_;

				private Builder() {
					url_ = "";
				}

				@Override
				public MessageLite build() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public com.google.protobuf.GeneratedMessageLite.Builder mergeFrom(GeneratedMessageLite arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public com.google.protobuf.AbstractMessageLite.Builder mergeFrom(CodedInputStream arg0,
						ExtensionRegistryLite arg1) throws IOException {
					// TODO Auto-generated method stub
					return null;
				}
			}
		}

		public static final class Content extends GeneratedMessageLite implements ContentOrBuilder {

			public static Content getDefaultInstance() {
				return defaultInstance;
			}

			private ByteString getLinkUrlBytes() {
				Object obj = linkUrl_;
				ByteString bytestring;
				if (obj instanceof String) {
					bytestring = ByteString.copyFromUtf8((String) obj);
					linkUrl_ = bytestring;
				} else {
					bytestring = (ByteString) obj;
				}
				return bytestring;
			}

			private ByteString getPhotoUrlBytes() {
				Object obj = photoUrl_;
				ByteString bytestring;
				if (obj instanceof String) {
					bytestring = ByteString.copyFromUtf8((String) obj);
					photoUrl_ = bytestring;
				} else {
					bytestring = (ByteString) obj;
				}
				return bytestring;
			}

			private ByteString getTextBytes() {
				Object obj = text_;
				ByteString bytestring;
				if (obj instanceof String) {
					bytestring = ByteString.copyFromUtf8((String) obj);
					text_ = bytestring;
				} else {
					bytestring = (ByteString) obj;
				}
				return bytestring;
			}

			public static Builder newBuilder() {
				return null;
			}

			public final Content getDefaultInstanceForType() {
				return defaultInstance;
			}

			public final String getLinkUrl() {
				Object obj = linkUrl_;
				String s1;
				if (obj instanceof String) {
					s1 = (String) obj;
				} else {
					ByteString bytestring = (ByteString) obj;
					String s = bytestring.toStringUtf8();
					if (Internal.isValidUtf8(bytestring))
						linkUrl_ = s;
					s1 = s;
				}
				return s1;
			}

			public final Location getLocation() {
				return location_;
			}

			public final PhotoMetadata getPhotoMetadata() {
				return photoMetadata_;
			}

			public final String getPhotoUrl() {
				Object obj = photoUrl_;
				String s1;
				if (obj instanceof String) {
					s1 = (String) obj;
				} else {
					ByteString bytestring = (ByteString) obj;
					String s = bytestring.toStringUtf8();
					if (Internal.isValidUtf8(bytestring))
						photoUrl_ = s;
					s1 = s;
				}
				return s1;
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
						k = 0 + CodedOutputStream.computeBytesSize(1, getTextBytes());
					if ((2 & bitField0_) == 2)
						k += CodedOutputStream.computeBytesSize(2, getPhotoUrlBytes());
					if ((4 & bitField0_) == 4)
						k += CodedOutputStream.computeBytesSize(3, getLinkUrlBytes());
					if ((8 & bitField0_) == 8)
						k += CodedOutputStream.computeMessageSize(4, location_);
					if ((0x10 & bitField0_) == 16)
						k += CodedOutputStream.computeMessageSize(5, photoMetadata_);
					memoizedSerializedSize = k;
					l = k;
				}
				return l;
			}

			public final String getText() {
				Object obj = text_;
				String s1;
				if (obj instanceof String) {
					s1 = (String) obj;
				} else {
					ByteString bytestring = (ByteString) obj;
					String s = bytestring.toStringUtf8();
					if (Internal.isValidUtf8(bytestring))
						text_ = s;
					s1 = s;
				}
				return s1;
			}

			public final boolean hasLinkUrl() {
				boolean flag;
				if ((4 & bitField0_) == 4)
					flag = true;
				else
					flag = false;
				return flag;
			}

			public final boolean hasLocation() {
				boolean flag;
				if ((8 & bitField0_) == 8)
					flag = true;
				else
					flag = false;
				return flag;
			}

			public final boolean hasPhotoMetadata() {
				boolean flag;
				if ((0x10 & bitField0_) == 16)
					flag = true;
				else
					flag = false;
				return flag;
			}

			public final boolean hasPhotoUrl() {
				boolean flag;
				if ((2 & bitField0_) == 2)
					flag = true;
				else
					flag = false;
				return flag;
			}

			public final boolean hasText() {
				boolean flag = true;
				if ((1 & bitField0_) != 1)
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
					codedoutputstream.writeBytes(1, getTextBytes());
				if ((2 & bitField0_) == 2)
					codedoutputstream.writeBytes(2, getPhotoUrlBytes());
				if ((4 & bitField0_) == 4)
					codedoutputstream.writeBytes(3, getLinkUrlBytes());
				if ((8 & bitField0_) == 8)
					codedoutputstream.writeMessage(4, location_);
				if ((0x10 & bitField0_) == 16)
					codedoutputstream.writeMessage(5, photoMetadata_);
			}

			private static final Content defaultInstance;
			private int bitField0_;
			private static Object linkUrl_;
			private static Location location_;
			private byte memoizedIsInitialized;
			private int memoizedSerializedSize;
			private static PhotoMetadata photoMetadata_;
			private static Object photoUrl_;
			private static Object text_;

			static {
				Content content = new Content();
				defaultInstance = content;
				content.text_ = "";
				content.photoUrl_ = "";
				content.linkUrl_ = "";
				content.location_ = Location.getDefaultInstance();
				content.photoMetadata_ = PhotoMetadata.getDefaultInstance();
			}

			private Content() {
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			private Content(Builder builder) {
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			Content(Builder builder, byte byte0) {
				this(builder);
			}

			public static final class Builder extends com.google.protobuf.GeneratedMessageLite.Builder implements
					ContentOrBuilder {

				public Builder clone() {
					return (new Builder()).mergeFrom(buildPartial());
				}

				public final Content build() {
					Content content = buildPartial();
					if (!isInitialized())
						throw new UninitializedMessageException(content);
					else
						return content;
				}

				public final Content buildPartial() {
					Content content = new Content(this, (byte) 0);
					int i = bitField0_;
					int j = i & 1;
					int k = 0;
					if (j == 1)
						k = 1;
					text_ = text_;
					if ((i & 2) == 2)
						k |= 2;
					photoUrl_ = photoUrl_;
					if ((i & 4) == 4)
						k |= 4;
					linkUrl_ = linkUrl_;
					if ((i & 8) == 8)
						k |= 8;
					location_ = location_;
					if ((i & 0x10) == 16)
						k |= 0x10;
					photoMetadata_ = photoMetadata_;
					bitField0_ = k;
					return content;
				}

				public final Builder clear() {
					super.clear();
					text_ = "";
					bitField0_ = -2 & bitField0_;
					photoUrl_ = "";
					bitField0_ = -3 & bitField0_;
					linkUrl_ = "";
					bitField0_ = -5 & bitField0_;
					location_ = Location.getDefaultInstance();
					bitField0_ = -9 & bitField0_;
					photoMetadata_ = PhotoMetadata.getDefaultInstance();
					bitField0_ = 0xffffffef & bitField0_;
					return this;
				}

				public final Builder clearLinkUrl() {
					bitField0_ = -5 & bitField0_;
					linkUrl_ = getDefaultInstance().getLinkUrl();
					return this;
				}

				public final Builder clearLocation() {
					location_ = Location.getDefaultInstance();
					bitField0_ = -9 & bitField0_;
					return this;
				}

				public final Builder clearPhotoMetadata() {
					photoMetadata_ = PhotoMetadata.getDefaultInstance();
					bitField0_ = 0xffffffef & bitField0_;
					return this;
				}

				public final Builder clearPhotoUrl() {
					bitField0_ = -3 & bitField0_;
					photoUrl_ = getDefaultInstance().getPhotoUrl();
					return this;
				}

				public final Builder clearText() {
					bitField0_ = -2 & bitField0_;
					text_ = getDefaultInstance().getText();
					return this;
				}

				public final Content getDefaultInstanceForType() {
					return getDefaultInstance();
				}

				public final String getLinkUrl() {
					Object obj = linkUrl_;
					String s;
					if (!(obj instanceof String)) {
						s = ((ByteString) obj).toStringUtf8();
						linkUrl_ = s;
					} else {
						s = (String) obj;
					}
					return s;
				}

				public final Location getLocation() {
					return location_;
				}

				public final PhotoMetadata getPhotoMetadata() {
					return photoMetadata_;
				}

				public final String getPhotoUrl() {
					Object obj = photoUrl_;
					String s;
					if (!(obj instanceof String)) {
						s = ((ByteString) obj).toStringUtf8();
						photoUrl_ = s;
					} else {
						s = (String) obj;
					}
					return s;
				}

				public final String getText() {
					Object obj = text_;
					String s;
					if (!(obj instanceof String)) {
						s = ((ByteString) obj).toStringUtf8();
						text_ = s;
					} else {
						s = (String) obj;
					}
					return s;
				}

				public final boolean hasLinkUrl() {
					boolean flag;
					if ((4 & bitField0_) == 4)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean hasLocation() {
					boolean flag;
					if ((8 & bitField0_) == 8)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean hasPhotoMetadata() {
					boolean flag;
					if ((0x10 & bitField0_) == 16)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean hasPhotoUrl() {
					boolean flag;
					if ((2 & bitField0_) == 2)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean hasText() {
					boolean flag = true;
					if ((1 & bitField0_) != 1)
						flag = false;
					return flag;
				}

				public final boolean isInitialized() {
					return true;
				}

				public final Builder mergeFrom(Content content) {
					if (content.hasText())
						setText(content.getText());
					if (content.hasPhotoUrl())
						setPhotoUrl(content.getPhotoUrl());
					if (content.hasLinkUrl())
						setLinkUrl(content.getLinkUrl());
					if (content.hasLocation()) {
						Location location = content.getLocation();
						if ((8 & bitField0_) == 8 && location_ != Location.getDefaultInstance())
							location_ = Location.newBuilder(location_).mergeFrom(location).buildPartial();
						else
							location_ = location;
						bitField0_ = 8 | bitField0_;
					}
					if (content.hasPhotoMetadata()) {
						PhotoMetadata photometadata = content.getPhotoMetadata();
						if ((0x10 & bitField0_) == 16 && photoMetadata_ != PhotoMetadata.getDefaultInstance())
							photoMetadata_ = PhotoMetadata.newBuilder(photoMetadata_).mergeFrom(photometadata)
									.buildPartial();
						else
							photoMetadata_ = photometadata;
						bitField0_ = 0x10 | bitField0_;
					}
					return this;
				}

				public final Builder setLinkUrl(String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						bitField0_ = 4 | bitField0_;
						linkUrl_ = s;
						return this;
					}
				}

				public final Builder setLocation(Location.Builder builder) {
					location_ = builder.build();
					bitField0_ = 8 | bitField0_;
					return this;
				}

				public final Builder setLocation(Location location) {
					if (location == null) {
						throw new NullPointerException();
					} else {
						location_ = location;
						bitField0_ = 8 | bitField0_;
						return this;
					}
				}

				public final Builder setPhotoMetadata(Builder builder) {
					photoMetadata_ = builder.build().getPhotoMetadata();
					bitField0_ = 0x10 | bitField0_;
					return this;
				}

				public final Builder setPhotoMetadata(PhotoMetadata photometadata) {
					if (photometadata == null) {
						throw new NullPointerException();
					} else {
						photoMetadata_ = photometadata;
						bitField0_ = 0x10 | bitField0_;
						return this;
					}
				}

				public final Builder setPhotoUrl(String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						bitField0_ = 2 | bitField0_;
						photoUrl_ = s;
						return this;
					}
				}

				public final Builder setText(String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						bitField0_ = 1 | bitField0_;
						text_ = s;
						return this;
					}
				}

				private int bitField0_;
				private Object linkUrl_;
				private Location location_;
				private PhotoMetadata photoMetadata_;
				private Object photoUrl_;
				private Object text_;

				private Builder() {
					text_ = "";
					photoUrl_ = "";
					linkUrl_ = "";
					location_ = Location.getDefaultInstance();
					photoMetadata_ = PhotoMetadata.getDefaultInstance();
				}

				@Override
				public com.google.protobuf.GeneratedMessageLite.Builder mergeFrom(GeneratedMessageLite arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public com.google.protobuf.AbstractMessageLite.Builder mergeFrom(CodedInputStream arg0,
						ExtensionRegistryLite arg1) throws IOException {
					// TODO Auto-generated method stub
					return null;
				}
			}
		}

		public static enum ResponseStatus implements com.google.protobuf.Internal.EnumLite {
			OK(1), ERROR(2), ERROR_CANNOT_CONTACT(3), NEED_ID(4), CONVERSATION_TOO_LARGE(5), ERROR_INVALID_CONTACT(6), ERROR_INVALID_EMAIL(
					7), ERROR_INVALID_PHONE(8), ERROR_COUNTRY_UNSUPPORTED(9), ERROR_INVALID_URL(10), ERROR_APP_BLOCKED(11), ERROR_EXCEED_SMS_INVITES(
					12), ERROR_ALREADY_IN_CONVERSATION(13), ERROR_USER_NOT_IN_CONVERSATION(14), ERROR_INVALID_REQUEST(15), ERROR_UNEXPECTED(
					16), ERROR_USER_MUST_BE_GAIA(17), ERROR_USER_NOT_FOUND(18), ERROR_DUPLICATE_REQUEST(19), ERROR_HANGOUT_INVITE_NOT_FOUND(
					20), ERROR_HANGOUT_INVITE_EXPIRED(21), ERROR_HANGOUT_INVITE_ALREADY_HANDLED(22), ERROR_HANGOUT_INVITE_NO_DEVICE_FOUND(
					23), ERROR_TEMPORARY(24);

			private final int value;

			private ResponseStatus(int value) {
				this.value = value;
			}

			public final int getNumber() {
				return value;
			}

			public static ResponseStatus valueOf(int value) {
				for (ResponseStatus r : ResponseStatus.values()) {
					if (value == r.value) {
						return r;
					}
				}
				return null;
			}
		}

		public static enum ConversationType implements com.google.protobuf.Internal.EnumLite {

			ONE_TO_ONE(1), GROUP(2);

			private int value;

			private ConversationType(int value) {
				this.value = value;
			}

			public final int getNumber() {
				return value;
			}

			public static ConversationType valueOf(int value) {
				for (ConversationType t : ConversationType.values()) {
					if (t.value == value) {
						return t;
					}
				}
				return null;
			}
		}

		public static interface StubbyInfoOrBuilder extends MessageLiteOrBuilder {

			public abstract ClientVersion getClientVersion();

			public abstract String getRecipientId(int i);

			public abstract int getRecipientIdCount();

			public abstract List getRecipientIdList();

			public abstract String getSenderId();

			public abstract boolean hasClientVersion();

			public abstract boolean hasSenderId();
		}

		// =======================================================================================
		//
		// =======================================================================================
		public static final class StubbyInfo extends GeneratedMessageLite implements StubbyInfoOrBuilder {

			private static final StubbyInfo defaultInstance;
			private int bitField0_;
			private Version.ClientVersion clientVersion_;
			private byte memoizedIsInitialized;
			private int memoizedSerializedSize;
			private LazyStringList recipientId_;
			private Object senderId_;

			static {
				StubbyInfo stubbyinfo = new StubbyInfo();
				defaultInstance = stubbyinfo;
				stubbyinfo.senderId_ = "";
				stubbyinfo.recipientId_ = LazyStringArrayList.EMPTY;
				stubbyinfo.clientVersion_ = Version.ClientVersion.getDefaultInstance();
			}

			private StubbyInfo() {
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			StubbyInfo(Builder builder) {
				super();
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			public static StubbyInfo getDefaultInstance() {
				return defaultInstance;
			}

			private ByteString getSenderIdBytes() {
				Object obj = senderId_;
				ByteString bytestring;
				if (obj instanceof String) {
					bytestring = ByteString.copyFromUtf8((String) obj);
					senderId_ = bytestring;
				} else {
					bytestring = (ByteString) obj;
				}
				return bytestring;
			}

			public static Builder newBuilder() {
				return new Builder();
			}

			public static Builder newBuilder(StubbyInfo stubbyinfo) {
				return new Builder().mergeFrom(stubbyinfo);
			}

			public final Version.ClientVersion getClientVersion() {
				return clientVersion_;
			}

			public final StubbyInfo getDefaultInstanceForType() {
				return defaultInstance;
			}

			public final String getRecipientId(int i) {
				return (String) recipientId_.get(i);
			}

			public final int getRecipientIdCount() {
				return recipientId_.size();
			}

			public final List getRecipientIdList() {
				return recipientId_;
			}

			public final String getSenderId() {
				Object obj = senderId_;
				String s1;
				if (obj instanceof String) {
					s1 = (String) obj;
				} else {
					ByteString bytestring = (ByteString) obj;
					String s = bytestring.toStringUtf8();
					if (Internal.isValidUtf8(bytestring))
						senderId_ = s;
					s1 = s;
				}
				return s1;
			}

			public final int getSerializedSize() {
				int i = memoizedSerializedSize;
				int k1;
				if (i != -1) {
					k1 = i;
				} else {
					int j = 1 & bitField0_;
					int k = 0;
					if (j == 1)
						k = 0 + CodedOutputStream.computeBytesSize(1, getSenderIdBytes());
					int l = 0;
					for (int i1 = 0; i1 < recipientId_.size(); i1++)
						l += CodedOutputStream.computeBytesSizeNoTag(recipientId_.getByteString(i1));

					int j1 = k + l + 1 * getRecipientIdList().size();
					if ((2 & bitField0_) == 2)
						j1 += CodedOutputStream.computeMessageSize(3, clientVersion_);
					memoizedSerializedSize = j1;
					k1 = j1;
				}
				return k1;
			}

			public final boolean hasClientVersion() {
				boolean flag;
				if ((2 & bitField0_) == 2)
					flag = true;
				else
					flag = false;
				return flag;
			}

			public final boolean hasSenderId() {
				boolean flag = true;
				if ((1 & bitField0_) != 1)
					flag = false;
				return flag;
			}

			public final boolean isInitialized() {
				boolean flag = true;
				byte byte0 = memoizedIsInitialized;
				if (byte0 != -1) {
					if (byte0 != 1)
						flag = false;
				} else {
					memoizedIsInitialized = 1;
				}
				return flag;
			}

			public final com.google.protobuf.MessageLite.Builder newBuilderForType() {
				return new Builder();
			}

			public final com.google.protobuf.MessageLite.Builder toBuilder() {
				return new Builder().mergeFrom(this);
			}

			protected final Object writeReplace() throws ObjectStreamException {
				return super.writeReplace();
			}

			public final void writeTo(CodedOutputStream codedoutputstream) throws IOException {
				getSerializedSize();
				if ((1 & bitField0_) == 1)
					codedoutputstream.writeBytes(1, getSenderIdBytes());
				for (int i = 0; i < recipientId_.size(); i++)
					codedoutputstream.writeBytes(2, recipientId_.getByteString(i));

				if ((2 & bitField0_) == 2)
					codedoutputstream.writeMessage(3, clientVersion_);
			}

			public static final class Builder extends com.google.protobuf.GeneratedMessageLite.Builder implements
					StubbyInfoOrBuilder {

				private int bitField0_;
				private Version.ClientVersion clientVersion_;
				private LazyStringList recipientId_;
				private Object senderId_;

				private Builder() {
					senderId_ = "";
					recipientId_ = LazyStringArrayList.EMPTY;
					clientVersion_ = Version.ClientVersion.getDefaultInstance();
				}

				public Builder clone() {
					return (new StubbyInfo.Builder()).mergeFrom(buildPartial());
				}

				private void ensureRecipientIdIsMutable() {
					if ((2 & bitField0_) != 2) {
						recipientId_ = new LazyStringArrayList(recipientId_);
						bitField0_ = 2 | bitField0_;
					}
				}

				public StubbyInfo.Builder mergeFrom(CodedInputStream codedinputstream,
						ExtensionRegistryLite extensionregistrylite) throws IOException {
					do {
						int i = codedinputstream.readTag();
						switch (i) {
							default:
								if (parseUnknownField(codedinputstream, extensionregistrylite, i))
									continue;
								// fall through

							case 0: // '\0'
								return this;

							case 10: // '\n'
								bitField0_ = 1 | bitField0_;
								senderId_ = codedinputstream.readBytes();
								break;

							case 18: // '\022'
								ensureRecipientIdIsMutable();
								recipientId_.add(codedinputstream.readBytes());
								break;

							case 26: // '\032'
								ClientVersion.Builder builder = Version.ClientVersion.newBuilder();
								if (hasClientVersion())
									builder.mergeFrom(getClientVersion());
								codedinputstream.readMessage(builder, extensionregistrylite);
								setClientVersion(builder.buildPartial());
								break;
						}
					} while (true);
				}

				public final StubbyInfo.Builder addAllRecipientId(Iterable iterable) {
					ensureRecipientIdIsMutable();
					com.google.protobuf.GeneratedMessageLite.Builder.addAll(iterable, recipientId_);
					return this;
				}

				public final StubbyInfo.Builder addRecipientId(String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						ensureRecipientIdIsMutable();
						recipientId_.add(s);
						return this;
					}
				}

				public final StubbyInfo build() {
					StubbyInfo stubbyinfo = buildPartial();
					if (!stubbyinfo.isInitialized())
						throw new UninitializedMessageException(stubbyinfo);
					else
						return stubbyinfo;
				}

				public final StubbyInfo buildPartial() {
					StubbyInfo stubbyinfo = new StubbyInfo(this);
					int i = bitField0_;
					int j = i & 1;
					int k = 0;
					if (j == 1)
						k = 1;
					stubbyinfo.senderId_ = senderId_;
					if ((2 & bitField0_) == 2) {
						recipientId_ = new UnmodifiableLazyStringList(recipientId_);
						bitField0_ = -3 & bitField0_;
					}
					stubbyinfo.recipientId_ = recipientId_;
					if ((i & 4) == 4)
						k |= 2;
					stubbyinfo.clientVersion_ = clientVersion_;
					stubbyinfo.bitField0_ = k;
					return stubbyinfo;
				}

				public final StubbyInfo.Builder clear() {
					super.clear();
					senderId_ = "";
					bitField0_ = -2 & bitField0_;
					recipientId_ = LazyStringArrayList.EMPTY;
					bitField0_ = -3 & bitField0_;
					clientVersion_ = Version.ClientVersion.getDefaultInstance();
					bitField0_ = -5 & bitField0_;
					return this;
				}

				public final StubbyInfo.Builder clearClientVersion() {
					clientVersion_ = Version.ClientVersion.getDefaultInstance();
					bitField0_ = -5 & bitField0_;
					return this;
				}

				public final StubbyInfo.Builder clearRecipientId() {
					recipientId_ = LazyStringArrayList.EMPTY;
					bitField0_ = -3 & bitField0_;
					return this;
				}

				public final StubbyInfo.Builder clearSenderId() {
					bitField0_ = -2 & bitField0_;
					senderId_ = StubbyInfo.getDefaultInstance().getSenderId();
					return this;
				}

				public final Version.ClientVersion getClientVersion() {
					return clientVersion_;
				}

				public final StubbyInfo getDefaultInstanceForType() {
					return StubbyInfo.getDefaultInstance();
				}

				public final String getRecipientId(int i) {
					return (String) recipientId_.get(i);
				}

				public final int getRecipientIdCount() {
					return recipientId_.size();
				}

				public final List getRecipientIdList() {
					return Collections.unmodifiableList(recipientId_);
				}

				public final String getSenderId() {
					Object obj = senderId_;
					String s;
					if (!(obj instanceof String)) {
						s = ((ByteString) obj).toStringUtf8();
						senderId_ = s;
					} else {
						s = (String) obj;
					}
					return s;
				}

				public final boolean hasClientVersion() {
					boolean flag;
					if ((4 & bitField0_) == 4)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean hasSenderId() {
					boolean flag = true;
					if ((1 & bitField0_) != 1)
						flag = false;
					return flag;
				}

				public final boolean isInitialized() {
					return true;
				}

				public final com.google.protobuf.GeneratedMessageLite.Builder mergeFrom(
						GeneratedMessageLite generatedmessagelite) {
					return mergeFrom((StubbyInfo) generatedmessagelite);
				}

				public final StubbyInfo.Builder mergeFrom(StubbyInfo stubbyinfo) {
					if (stubbyinfo == StubbyInfo.getDefaultInstance()) {
						return this;
					}

					if (stubbyinfo.hasSenderId())
						setSenderId(stubbyinfo.getSenderId());
					if (!stubbyinfo.recipientId_.isEmpty())
						if (recipientId_.isEmpty()) {
							recipientId_ = stubbyinfo.recipientId_;
							bitField0_ = -3 & bitField0_;
						} else {
							ensureRecipientIdIsMutable();
							recipientId_.addAll(stubbyinfo.recipientId_);
						}
					if (stubbyinfo.hasClientVersion()) {
						Version.ClientVersion clientversion = stubbyinfo.getClientVersion();
						if ((4 & bitField0_) == 4 && clientVersion_ != Version.ClientVersion.getDefaultInstance())
							clientVersion_ = (Version.ClientVersion) Version.ClientVersion.newBuilder(clientVersion_)
									.mergeFrom(clientversion).buildPartial();
						else
							clientVersion_ = clientversion;
						bitField0_ = 4 | bitField0_;
					}
					return this;
				}

				public final StubbyInfo.Builder setClientVersion(Version.ClientVersion.Builder builder) {
					clientVersion_ = (ClientVersion) builder.build();
					bitField0_ = 4 | bitField0_;
					return this;
				}

				public final StubbyInfo.Builder setClientVersion(Version.ClientVersion clientversion) {
					if (clientversion == null) {
						throw new NullPointerException();
					} else {
						clientVersion_ = clientversion;
						bitField0_ = 4 | bitField0_;
						return this;
					}
				}

				public final StubbyInfo.Builder setRecipientId(int i, String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						ensureRecipientIdIsMutable();
						recipientId_.set(i, s);
						return this;
					}
				}

				public final StubbyInfo.Builder setSenderId(String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						bitField0_ = 1 | bitField0_;
						senderId_ = s;
						return this;
					}
				}
			}
		}

		public static interface ConversationMetadataOrBuilder extends MessageLiteOrBuilder {

			public abstract String getJoinCode();

			public abstract Location getLocation();

			public abstract String getTag(int i);

			public abstract int getTagCount();

			public abstract List getTagList();

			public abstract ConversationMetadata.ConversationVisibility getVisibility();

			public abstract boolean hasJoinCode();

			public abstract boolean hasLocation();

			public abstract boolean hasVisibility();
		}

		public static final class ConversationMetadata extends GeneratedMessageLite implements
				ConversationMetadataOrBuilder {

			private static final ConversationMetadata defaultInstance;
			private int bitField0_;
			private Object joinCode_;
			private Location location_;
			private byte memoizedIsInitialized;
			private int memoizedSerializedSize;
			private LazyStringList tag_;
			private ConversationVisibility visibility_;

			static {
				ConversationMetadata conversationmetadata = new ConversationMetadata();
				defaultInstance = conversationmetadata;
				conversationmetadata.visibility_ = ConversationVisibility.PUBLIC;
				conversationmetadata.location_ = Location.getDefaultInstance();
				conversationmetadata.tag_ = LazyStringArrayList.EMPTY;
				conversationmetadata.joinCode_ = "";
			}

			private ConversationMetadata() {
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			ConversationMetadata(Builder builder) {
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			public static ConversationMetadata getDefaultInstance() {
				return defaultInstance;
			}

			private ByteString getJoinCodeBytes() {
				Object obj = joinCode_;
				ByteString bytestring;
				if (obj instanceof String) {
					bytestring = ByteString.copyFromUtf8((String) obj);
					joinCode_ = bytestring;
				} else {
					bytestring = (ByteString) obj;
				}
				return bytestring;
			}

			public static Builder newBuilder() {
				return new Builder();
			}

			public static Builder newBuilder(ConversationMetadata conversationmetadata) {
				return new Builder().mergeFrom(conversationmetadata);
			}

			public final ConversationMetadata getDefaultInstanceForType() {
				return defaultInstance;
			}

			public final String getJoinCode() {
				Object obj = joinCode_;
				String s1;
				if (obj instanceof String) {
					s1 = (String) obj;
				} else {
					ByteString bytestring = (ByteString) obj;
					String s = bytestring.toStringUtf8();
					if (Internal.isValidUtf8(bytestring))
						joinCode_ = s;
					s1 = s;
				}
				return s1;
			}

			public final Location getLocation() {
				return location_;
			}

			public final int getSerializedSize() {
				int i = memoizedSerializedSize;
				int k1;
				if (i != -1) {
					k1 = i;
				} else {
					int j = 1 & bitField0_;
					int k = 0;
					if (j == 1)
						k = 0 + CodedOutputStream.computeEnumSize(1, visibility_.getNumber());
					if ((2 & bitField0_) == 2)
						k += CodedOutputStream.computeMessageSize(2, location_);
					int l = 0;
					for (int i1 = 0; i1 < tag_.size(); i1++)
						l += CodedOutputStream.computeBytesSizeNoTag(tag_.getByteString(i1));

					int j1 = k + l + 1 * getTagList().size();
					if ((4 & bitField0_) == 4)
						j1 += CodedOutputStream.computeBytesSize(4, getJoinCodeBytes());
					memoizedSerializedSize = j1;
					k1 = j1;
				}
				return k1;
			}

			public final String getTag(int i) {
				return (String) tag_.get(i);
			}

			public final int getTagCount() {
				return tag_.size();
			}

			public final List getTagList() {
				return tag_;
			}

			public final ConversationVisibility getVisibility() {
				return visibility_;
			}

			public final boolean hasJoinCode() {
				boolean flag;
				if ((4 & bitField0_) == 4)
					flag = true;
				else
					flag = false;
				return flag;
			}

			public final boolean hasLocation() {
				boolean flag;
				if ((2 & bitField0_) == 2)
					flag = true;
				else
					flag = false;
				return flag;
			}

			public final boolean hasVisibility() {
				boolean flag = true;
				if ((1 & bitField0_) != 1)
					flag = false;
				return flag;
			}

			public final boolean isInitialized() {
				boolean flag = true;
				byte byte0 = memoizedIsInitialized;
				if (byte0 != -1) {
					if (byte0 != 1)
						flag = false;
				} else {
					memoizedIsInitialized = 1;
				}
				return flag;
			}

			protected final Object writeReplace() throws ObjectStreamException {
				return super.writeReplace();
			}

			public final void writeTo(CodedOutputStream codedoutputstream) throws IOException {
				getSerializedSize();
				if ((1 & bitField0_) == 1)
					codedoutputstream.writeEnum(1, visibility_.getNumber());
				if ((2 & bitField0_) == 2)
					codedoutputstream.writeMessage(2, location_);
				for (int i = 0; i < tag_.size(); i++)
					codedoutputstream.writeBytes(3, tag_.getByteString(i));

				if ((4 & bitField0_) == 4)
					codedoutputstream.writeBytes(4, getJoinCodeBytes());
			}

			public static enum ConversationVisibility implements com.google.protobuf.Internal.EnumLite {
				PUBLIC(1), PRIVATE(2);

				private int value;

				private ConversationVisibility(int value) {
					this.value = value;
				}

				public final int getNumber() {
					return value;
				}

				public static ConversationVisibility valueOf(int value) {
					for (ConversationVisibility v : ConversationVisibility.values()) {
						if (v.value == value) {
							return v;
						}
					}
					return null;
				}
			}

			public static final class Builder extends com.google.protobuf.GeneratedMessageLite.Builder implements
					ConversationMetadataOrBuilder {

				private int bitField0_;
				private Object joinCode_;
				private Location location_;
				private LazyStringList tag_;
				private ConversationMetadata.ConversationVisibility visibility_;

				private Builder() {
					visibility_ = ConversationMetadata.ConversationVisibility.PUBLIC;
					location_ = Location.getDefaultInstance();
					tag_ = LazyStringArrayList.EMPTY;
					joinCode_ = "";
				}

				public Builder clone() {
					return (new Builder()).mergeFrom(buildPartial());
				}

				private void ensureTagIsMutable() {
					if ((4 & bitField0_) != 4) {
						tag_ = new LazyStringArrayList(tag_);
						bitField0_ = 4 | bitField0_;
					}
				}

				public Builder mergeFrom(CodedInputStream codedinputstream, ExtensionRegistryLite extensionregistrylite)
					throws IOException {
					do {
						int i = codedinputstream.readTag();
						switch (i) {
							default:
								if (parseUnknownField(codedinputstream, extensionregistrylite, i))
									continue;
								// fall through

							case 0: // '\0'
								return this;

							case 8: // '\b'
								ConversationMetadata.ConversationVisibility conversationvisibility = ConversationMetadata.ConversationVisibility
										.valueOf(codedinputstream.readEnum());
								if (conversationvisibility != null) {
									bitField0_ = 1 | bitField0_;
									visibility_ = conversationvisibility;
								}
								break;

							case 18: // '\022'
								Location.Builder builder = Location.newBuilder();
								if (hasLocation())
									builder.mergeFrom(getLocation());
								codedinputstream.readMessage(builder, extensionregistrylite);
								setLocation(builder.buildPartial());
								break;

							case 26: // '\032'
								ensureTagIsMutable();
								tag_.add(codedinputstream.readBytes());
								break;

							case 34: // '"'
								bitField0_ = 8 | bitField0_;
								joinCode_ = codedinputstream.readBytes();
								break;
						}
					} while (true);
				}

				public final Builder addAllTag(Iterable iterable) {
					ensureTagIsMutable();
					com.google.protobuf.GeneratedMessageLite.Builder.addAll(iterable, tag_);
					return this;
				}

				public final Builder addTag(String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						ensureTagIsMutable();
						tag_.add(s);
						return this;
					}
				}

				public final ConversationMetadata build() {
					ConversationMetadata conversationmetadata = buildPartial();
					if (!conversationmetadata.isInitialized())
						throw new UninitializedMessageException(tag_);
					else
						return conversationmetadata;
				}

				public final ConversationMetadata buildPartial() {
					ConversationMetadata conversationmetadata = new ConversationMetadata();
					int i = bitField0_;
					int j = i & 1;
					int k = 0;
					if (j == 1)
						k = 1;
					conversationmetadata.visibility_ = visibility_;
					if ((i & 2) == 2)
						k |= 2;
					conversationmetadata.location_ = location_;
					if ((4 & bitField0_) == 4) {
						tag_ = new UnmodifiableLazyStringList(tag_);
						bitField0_ = -5 & bitField0_;
					}
					conversationmetadata.tag_ = tag_;
					if ((i & 8) == 8)
						k |= 4;
					conversationmetadata.joinCode_ = joinCode_;
					conversationmetadata.bitField0_ = k;
					return conversationmetadata;
				}

				public final Builder clear() {
					super.clear();
					visibility_ = ConversationMetadata.ConversationVisibility.PUBLIC;
					bitField0_ = -2 & bitField0_;
					location_ = Location.getDefaultInstance();
					bitField0_ = -3 & bitField0_;
					tag_ = LazyStringArrayList.EMPTY;
					bitField0_ = -5 & bitField0_;
					joinCode_ = "";
					bitField0_ = -9 & bitField0_;
					return this;
				}

				public final Builder clearJoinCode() {
					bitField0_ = -9 & bitField0_;
					joinCode_ = ConversationMetadata.getDefaultInstance().getJoinCode();
					return this;
				}

				public final Builder clearLocation() {
					location_ = Location.getDefaultInstance();
					bitField0_ = -3 & bitField0_;
					return this;
				}

				public final Builder clearTag() {
					tag_ = LazyStringArrayList.EMPTY;
					bitField0_ = -5 & bitField0_;
					return this;
				}

				public final Builder clearVisibility() {
					bitField0_ = -2 & bitField0_;
					visibility_ = ConversationMetadata.ConversationVisibility.PUBLIC;
					return this;
				}

				public final ConversationMetadata getDefaultInstanceForType() {
					return ConversationMetadata.getDefaultInstance();
				}

				public final String getJoinCode() {
					Object obj = joinCode_;
					String s;
					if (!(obj instanceof String)) {
						s = ((ByteString) obj).toStringUtf8();
						joinCode_ = s;
					} else {
						s = (String) obj;
					}
					return s;
				}

				public final Location getLocation() {
					return location_;
				}

				public final String getTag(int i) {
					return (String) tag_.get(i);
				}

				public final int getTagCount() {
					return tag_.size();
				}

				public final List getTagList() {
					return Collections.unmodifiableList(tag_);
				}

				public final ConversationMetadata.ConversationVisibility getVisibility() {
					return visibility_;
				}

				public final boolean hasJoinCode() {
					boolean flag;
					if ((8 & bitField0_) == 8)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean hasLocation() {
					boolean flag;
					if ((2 & bitField0_) == 2)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean hasVisibility() {
					boolean flag = true;
					if ((1 & bitField0_) != 1)
						flag = false;
					return flag;
				}

				public final boolean isInitialized() {
					return true;
				}

				public final Builder mergeFrom(ConversationMetadata conversationmetadata) {
					if (conversationmetadata != ConversationMetadata.getDefaultInstance()) {
						if (conversationmetadata.hasVisibility())
							setVisibility(conversationmetadata.getVisibility());
						if (conversationmetadata.hasLocation()) {
							Location location = conversationmetadata.getLocation();
							if ((2 & bitField0_) == 2 && location_ != Location.getDefaultInstance())
								location_ = (Location) Location.newBuilder(location_).mergeFrom(location).buildPartial();
							else
								location_ = location;
							bitField0_ = 2 | bitField0_;
						}
						if (!conversationmetadata.tag_.isEmpty())
							if (tag_.isEmpty()) {
								tag_ = conversationmetadata.tag_;
								bitField0_ = -5 & bitField0_;
							} else {
								ensureTagIsMutable();
								tag_.addAll(conversationmetadata.tag_);
							}
						if (conversationmetadata.hasJoinCode())
							setJoinCode(conversationmetadata.getJoinCode());
					}
					return this;
				}

				public final Builder setJoinCode(String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						bitField0_ = 8 | bitField0_;
						joinCode_ = s;
						return this;
					}
				}

				public final Builder setLocation(Location.Builder builder) {
					location_ = (Location) builder.build();
					bitField0_ = 2 | bitField0_;
					return this;
				}

				public final Builder setLocation(Location location) {
					if (location == null) {
						throw new NullPointerException();
					} else {
						location_ = location;
						bitField0_ = 2 | bitField0_;
						return this;
					}
				}

				public final Builder setTag(int i, String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						ensureTagIsMutable();
						tag_.set(i, s);
						return this;
					}
				}

				public final Builder setVisibility(ConversationMetadata.ConversationVisibility conversationvisibility) {
					if (conversationvisibility == null) {
						throw new NullPointerException();
					} else {
						bitField0_ = 1 | bitField0_;
						visibility_ = conversationvisibility;
						return this;
					}
				}

				@Override
				public com.google.protobuf.GeneratedMessageLite.Builder mergeFrom(GeneratedMessageLite arg0) {
					// TODO Auto-generated method stub
					return null;
				}
			}

			@Override
			public com.google.protobuf.MessageLite.Builder newBuilderForType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public com.google.protobuf.MessageLite.Builder toBuilder() {
				// TODO Auto-generated method stub
				return null;
			}
		}

		public static interface ParticipantOrBuilder extends MessageLiteOrBuilder {

			public abstract String getFirstName();

			public abstract String getFullName();

			public abstract long getLastSeenAt();

			public abstract String getParticipantId();

			public abstract String getProfilePhotoUrl();

			public abstract Participant.Type getType();

			public abstract boolean hasFirstName();

			public abstract boolean hasFullName();

			public abstract boolean hasLastSeenAt();

			public abstract boolean hasParticipantId();

			public abstract boolean hasProfilePhotoUrl();

			public abstract boolean hasType();
		}

		public static final class Participant extends GeneratedMessageLite implements ParticipantOrBuilder {

			private static final Participant defaultInstance;
			private int bitField0_;
			private Object firstName_;
			private Object fullName_;
			private long lastSeenAt_;
			private byte memoizedIsInitialized;
			private int memoizedSerializedSize;
			private Object participantId_;
			private Object profilePhotoUrl_;
			private Type type_;

			static {
				Participant participant = new Participant();
				defaultInstance = participant;
				participant.participantId_ = "";
				participant.lastSeenAt_ = 0L;
				participant.fullName_ = "";
				participant.firstName_ = "";
				participant.type_ = Type.INVITED;
				participant.profilePhotoUrl_ = "";
			}

			private Participant() {
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			Participant(Builder builder) {
				super();
				memoizedIsInitialized = -1;
				memoizedSerializedSize = -1;
			}

			public static Participant getDefaultInstance() {
				return defaultInstance;
			}

			private ByteString getFirstNameBytes() {
				Object obj = firstName_;
				ByteString bytestring;
				if (obj instanceof String) {
					bytestring = ByteString.copyFromUtf8((String) obj);
					firstName_ = bytestring;
				} else {
					bytestring = (ByteString) obj;
				}
				return bytestring;
			}

			private ByteString getFullNameBytes() {
				Object obj = fullName_;
				ByteString bytestring;
				if (obj instanceof String) {
					bytestring = ByteString.copyFromUtf8((String) obj);
					fullName_ = bytestring;
				} else {
					bytestring = (ByteString) obj;
				}
				return bytestring;
			}

			private ByteString getParticipantIdBytes() {
				Object obj = participantId_;
				ByteString bytestring;
				if (obj instanceof String) {
					bytestring = ByteString.copyFromUtf8((String) obj);
					participantId_ = bytestring;
				} else {
					bytestring = (ByteString) obj;
				}
				return bytestring;
			}

			private ByteString getProfilePhotoUrlBytes() {
				Object obj = profilePhotoUrl_;
				ByteString bytestring;
				if (obj instanceof String) {
					bytestring = ByteString.copyFromUtf8((String) obj);
					profilePhotoUrl_ = bytestring;
				} else {
					bytestring = (ByteString) obj;
				}
				return bytestring;
			}

			public static Builder newBuilder() {
				return new Builder();
			}

			public static Builder newBuilder(Participant participant) {
				return new Builder().mergeFrom(participant);
			}

			public final Participant getDefaultInstanceForType() {
				return defaultInstance;
			}

			public final String getFirstName() {
				Object obj = firstName_;
				String s1;
				if (obj instanceof String) {
					s1 = (String) obj;
				} else {
					ByteString bytestring = (ByteString) obj;
					String s = bytestring.toStringUtf8();
					if (Internal.isValidUtf8(bytestring))
						firstName_ = s;
					s1 = s;
				}
				return s1;
			}

			public final String getFullName() {
				Object obj = fullName_;
				String s1;
				if (obj instanceof String) {
					s1 = (String) obj;
				} else {
					ByteString bytestring = (ByteString) obj;
					String s = bytestring.toStringUtf8();
					if (Internal.isValidUtf8(bytestring))
						fullName_ = s;
					s1 = s;
				}
				return s1;
			}

			public final long getLastSeenAt() {
				return lastSeenAt_;
			}

			public final String getParticipantId() {
				Object obj = participantId_;
				String s1;
				if (obj instanceof String) {
					s1 = (String) obj;
				} else {
					ByteString bytestring = (ByteString) obj;
					String s = bytestring.toStringUtf8();
					if (Internal.isValidUtf8(bytestring))
						participantId_ = s;
					s1 = s;
				}
				return s1;
			}

			public final String getProfilePhotoUrl() {
				Object obj = profilePhotoUrl_;
				String s1;
				if (obj instanceof String) {
					s1 = (String) obj;
				} else {
					ByteString bytestring = (ByteString) obj;
					String s = bytestring.toStringUtf8();
					if (Internal.isValidUtf8(bytestring))
						profilePhotoUrl_ = s;
					s1 = s;
				}
				return s1;
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
						k = 0 + CodedOutputStream.computeBytesSize(1, getParticipantIdBytes());
					if ((2 & bitField0_) == 2)
						k += CodedOutputStream.computeInt64Size(2, lastSeenAt_);
					if ((4 & bitField0_) == 4)
						k += CodedOutputStream.computeBytesSize(3, getFullNameBytes());
					if ((8 & bitField0_) == 8)
						k += CodedOutputStream.computeBytesSize(4, getFirstNameBytes());
					if ((0x10 & bitField0_) == 16)
						k += CodedOutputStream.computeEnumSize(5, type_.getNumber());
					if ((0x20 & bitField0_) == 32)
						k += CodedOutputStream.computeBytesSize(6, getProfilePhotoUrlBytes());
					memoizedSerializedSize = k;
					l = k;
				}
				return l;
			}

			public final Type getType() {
				return type_;
			}

			public final boolean hasFirstName() {
				boolean flag;
				if ((8 & bitField0_) == 8)
					flag = true;
				else
					flag = false;
				return flag;
			}

			public final boolean hasFullName() {
				boolean flag;
				if ((4 & bitField0_) == 4)
					flag = true;
				else
					flag = false;
				return flag;
			}

			public final boolean hasLastSeenAt() {
				boolean flag;
				if ((2 & bitField0_) == 2)
					flag = true;
				else
					flag = false;
				return flag;
			}

			public final boolean hasParticipantId() {
				boolean flag = true;
				if ((1 & bitField0_) != 1)
					flag = false;
				return flag;
			}

			public final boolean hasProfilePhotoUrl() {
				boolean flag;
				if ((0x20 & bitField0_) == 32)
					flag = true;
				else
					flag = false;
				return flag;
			}

			public final boolean hasType() {
				boolean flag;
				if ((0x10 & bitField0_) == 16)
					flag = true;
				else
					flag = false;
				return flag;
			}

			public final boolean isInitialized() {
				boolean flag = true;
				byte byte0 = memoizedIsInitialized;
				if (byte0 != -1) {
					if (byte0 != 1)
						flag = false;
				}
				return flag;
			}

			public final com.google.protobuf.MessageLite.Builder newBuilderForType() {
				return new Builder();
			}

			public final com.google.protobuf.MessageLite.Builder toBuilder() {
				return new Builder().mergeFrom(this);
			}

			protected final Object writeReplace() throws ObjectStreamException {
				return super.writeReplace();
			}

			public final void writeTo(CodedOutputStream codedoutputstream) throws IOException {
				getSerializedSize();
				if ((1 & bitField0_) == 1)
					codedoutputstream.writeBytes(1, getParticipantIdBytes());
				if ((2 & bitField0_) == 2)
					codedoutputstream.writeInt64(2, lastSeenAt_);
				if ((4 & bitField0_) == 4)
					codedoutputstream.writeBytes(3, getFullNameBytes());
				if ((8 & bitField0_) == 8)
					codedoutputstream.writeBytes(4, getFirstNameBytes());
				if ((0x10 & bitField0_) == 16)
					codedoutputstream.writeEnum(5, type_.getNumber());
				if ((0x20 & bitField0_) == 32)
					codedoutputstream.writeBytes(6, getProfilePhotoUrlBytes());
			}

			public static enum Type implements com.google.protobuf.Internal.EnumLite {

				INVITED(1), SMS(2), ANDROID(3), IPHONE(4), WEB(5);

				private int value;

				private Type(int value) {
					this.value = value;
				}

				public static Type valueOf(int value) {
					for (Type t : Type.values()) {
						if (t.value == value) {
							return t;
						}
					}
					return null;
				}

				public final int getNumber() {
					return value;
				}
			}

			public static final class Builder extends com.google.protobuf.GeneratedMessageLite.Builder implements
					ParticipantOrBuilder {

				private int bitField0_;
				private Object firstName_;
				private Object fullName_;
				private long lastSeenAt_;
				private Object participantId_;
				private Object profilePhotoUrl_;
				private Participant.Type type_;

				private Builder() {
					participantId_ = "";
					fullName_ = "";
					firstName_ = "";
					type_ = Participant.Type.INVITED;
					profilePhotoUrl_ = "";
				}

				public Builder clone() {
					return (new Builder()).mergeFrom(buildPartial());
				}

				public Builder mergeFrom(CodedInputStream codedinputstream, ExtensionRegistryLite extensionregistrylite)
					throws IOException {
					do {
						int i = codedinputstream.readTag();
						switch (i) {
							default:
								if (parseUnknownField(codedinputstream, extensionregistrylite, i))
									continue;
								// fall through

							case 0: // '\0'
								return this;

							case 10: // '\n'
								bitField0_ = 1 | bitField0_;
								participantId_ = codedinputstream.readBytes();
								break;

							case 16: // '\020'
								bitField0_ = 2 | bitField0_;
								lastSeenAt_ = codedinputstream.readInt64();
								break;

							case 26: // '\032'
								bitField0_ = 4 | bitField0_;
								fullName_ = codedinputstream.readBytes();
								break;

							case 34: // '"'
								bitField0_ = 8 | bitField0_;
								firstName_ = codedinputstream.readBytes();
								break;

							case 40: // '('
								Participant.Type type = Participant.Type.valueOf(codedinputstream.readEnum());
								if (type != null) {
									bitField0_ = 0x10 | bitField0_;
									type_ = type;
								}
								break;

							case 50: // '2'
								bitField0_ = 0x20 | bitField0_;
								profilePhotoUrl_ = codedinputstream.readBytes();
								break;
						}
					} while (true);
				}

				public final Participant build() {
					Participant participant = buildPartial();
					if (!participant.isInitialized())
						throw new UninitializedMessageException(participant);
					else
						return participant;
				}

				public final Participant buildPartial() {
					Participant participant = new Participant(this);
					int i = bitField0_;
					int j = i & 1;
					int k = 0;
					if (j == 1)
						k = 1;
					participant.participantId_ = participantId_;
					if ((i & 2) == 2)
						k |= 2;
					participant.lastSeenAt_ = lastSeenAt_;
					if ((i & 4) == 4)
						k |= 4;
					participant.fullName_ = fullName_;
					if ((i & 8) == 8)
						k |= 8;
					participant.firstName_ = firstName_;
					if ((i & 0x10) == 16)
						k |= 0x10;
					participant.type_ = type_;
					if ((i & 0x20) == 32)
						k |= 0x20;
					participant.profilePhotoUrl_ = profilePhotoUrl_;
					participant.bitField0_ = k;
					return participant;
				}

				public final Builder clear() {
					super.clear();
					participantId_ = "";
					bitField0_ = -2 & bitField0_;
					lastSeenAt_ = 0L;
					bitField0_ = -3 & bitField0_;
					fullName_ = "";
					bitField0_ = -5 & bitField0_;
					firstName_ = "";
					bitField0_ = -9 & bitField0_;
					type_ = Participant.Type.INVITED;
					bitField0_ = 0xffffffef & bitField0_;
					profilePhotoUrl_ = "";
					bitField0_ = 0xffffffdf & bitField0_;
					return this;
				}

				public final Builder clearFirstName() {
					bitField0_ = -9 & bitField0_;
					firstName_ = Participant.getDefaultInstance().getFirstName();
					return this;
				}

				public final Builder clearFullName() {
					bitField0_ = -5 & bitField0_;
					fullName_ = Participant.getDefaultInstance().getFullName();
					return this;
				}

				public final Builder clearLastSeenAt() {
					bitField0_ = -3 & bitField0_;
					lastSeenAt_ = 0L;
					return this;
				}

				public final Builder clearParticipantId() {
					bitField0_ = -2 & bitField0_;
					participantId_ = Participant.getDefaultInstance().getParticipantId();
					return this;
				}

				public final Builder clearProfilePhotoUrl() {
					bitField0_ = 0xffffffdf & bitField0_;
					profilePhotoUrl_ = Participant.getDefaultInstance().getProfilePhotoUrl();
					return this;
				}

				public final Builder clearType() {
					bitField0_ = 0xffffffef & bitField0_;
					type_ = Participant.Type.INVITED;
					return this;
				}

				public final Participant getDefaultInstanceForType() {
					return Participant.getDefaultInstance();
				}

				public final String getFirstName() {
					Object obj = firstName_;
					String s;
					if (!(obj instanceof String)) {
						s = ((ByteString) obj).toStringUtf8();
						firstName_ = s;
					} else {
						s = (String) obj;
					}
					return s;
				}

				public final String getFullName() {
					Object obj = fullName_;
					String s;
					if (!(obj instanceof String)) {
						s = ((ByteString) obj).toStringUtf8();
						fullName_ = s;
					} else {
						s = (String) obj;
					}
					return s;
				}

				public final long getLastSeenAt() {
					return lastSeenAt_;
				}

				public final String getParticipantId() {
					Object obj = participantId_;
					String s;
					if (!(obj instanceof String)) {
						s = ((ByteString) obj).toStringUtf8();
						participantId_ = s;
					} else {
						s = (String) obj;
					}
					return s;
				}

				public final String getProfilePhotoUrl() {
					Object obj = profilePhotoUrl_;
					String s;
					if (!(obj instanceof String)) {
						s = ((ByteString) obj).toStringUtf8();
						profilePhotoUrl_ = s;
					} else {
						s = (String) obj;
					}
					return s;
				}

				public final Participant.Type getType() {
					return type_;
				}

				public final boolean hasFirstName() {
					boolean flag;
					if ((8 & bitField0_) == 8)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean hasFullName() {
					boolean flag;
					if ((4 & bitField0_) == 4)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean hasLastSeenAt() {
					boolean flag;
					if ((2 & bitField0_) == 2)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean hasParticipantId() {
					boolean flag = true;
					if ((1 & bitField0_) != 1)
						flag = false;
					return flag;
				}

				public final boolean hasProfilePhotoUrl() {
					boolean flag;
					if ((0x20 & bitField0_) == 32)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean hasType() {
					boolean flag;
					if ((0x10 & bitField0_) == 16)
						flag = true;
					else
						flag = false;
					return flag;
				}

				public final boolean isInitialized() {
					return true;
				}

				public final com.google.protobuf.GeneratedMessageLite.Builder mergeFrom(
						GeneratedMessageLite generatedmessagelite) {
					return mergeFrom((Participant) generatedmessagelite);
				}

				public final Builder mergeFrom(Participant participant) {
					if (participant == Participant.getDefaultInstance()) {
						return this;
					}
					if (participant.hasParticipantId())

						setParticipantId(participant.getParticipantId());
					if (participant.hasLastSeenAt())
						setLastSeenAt(participant.getLastSeenAt());
					if (participant.hasFullName())
						setFullName(participant.getFullName());
					if (participant.hasFirstName())
						setFirstName(participant.getFirstName());
					if (participant.hasType())
						setType(participant.getType());
					if (participant.hasProfilePhotoUrl())
						setProfilePhotoUrl(participant.getProfilePhotoUrl());
					return this;
				}

				public final Builder setFirstName(String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						bitField0_ = 8 | bitField0_;
						firstName_ = s;
						return this;
					}
				}

				public final Builder setFullName(String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						bitField0_ = 4 | bitField0_;
						fullName_ = s;
						return this;
					}
				}

				public final Builder setLastSeenAt(long l) {
					bitField0_ = 2 | bitField0_;
					lastSeenAt_ = l;
					return this;
				}

				public final Builder setParticipantId(String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						bitField0_ = 1 | bitField0_;
						participantId_ = s;
						return this;
					}
				}

				public final Builder setProfilePhotoUrl(String s) {
					if (s == null) {
						throw new NullPointerException();
					} else {
						bitField0_ = 0x20 | bitField0_;
						profilePhotoUrl_ = s;
						return this;
					}
				}

				public final Builder setType(Participant.Type type) {
					if (type == null) {
						throw new NullPointerException();
					} else {
						bitField0_ = 0x10 | bitField0_;
						type_ = type;
						return this;
					}
				}
			}
		}
}
