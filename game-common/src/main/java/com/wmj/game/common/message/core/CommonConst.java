// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: CommonConst.proto

package com.wmj.game.common.message.core;

public final class CommonConst {
  private CommonConst() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\021CommonConst.proto*\260\001\n\010CmdLimit\022\010\n\004Zero" +
      "\020\000\022\022\n\016SystemBeginCmd\020\001\022\021\n\014SystemEndCmd\020\210" +
      "\'\022\024\n\017GatewayBeginCmd\020\211\'\022\022\n\rGatewayEndCmd" +
      "\020\220N\022\021\n\014HallBeginCmd\020\221N\022\020\n\nHallEndCmd\020\240\234\001" +
      "\022\022\n\014GameBeginCmd\020\241\234\001\022\020\n\nGameEndCmd\020\240\215\006*\245" +
      "\001\n\003Cmd\022\n\n\006UnKnow\020\000\022\030\n\023ChangeRoleServerRe" +
      "q\020\355\007\022\030\n\023ChangeRoleServerRes\020\356\007\022\t\n\004Ping\020\211" +
      "\'\022\t\n\004Pong\020\212\'\022\016\n\tReconnect\020\213\'\022\r\n\010LoginReq" +
      "\020\221N\022\r\n\010LoginRes\020\222N\022\014\n\007QuitReq\020\227N\022\014\n\007Quit" +
      "Res\020\230NB$\n com.wmj.game.common.message.co" +
      "reP\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}
