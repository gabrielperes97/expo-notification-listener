import ExpoModulesCore

public class ExpoNotificationListenerModule: Module {
  public func definition() -> ModuleDefinition {
    Name("ExpoNotificationListener")

    Function("getApiKey") { () -> String in
      return "api-key"
    }
  }
}
