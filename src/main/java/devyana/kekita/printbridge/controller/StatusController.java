package devyana.kekita.printbridge.controller;

import devyana.kekita.printbridge.api.ApiResponse;
import devyana.kekita.printbridge.api.StatusData;
import devyana.kekita.printbridge.config.AppConfig;
import io.javalin.http.Context;

public class StatusController {

    public void status(Context ctx) {

        StatusData data = new StatusData(
                AppConfig.get().getApplication().getName(),
                AppConfig.get().getApplication().getVersion()
        );

        ApiResponse<StatusData> response = new ApiResponse<>(
                true,
                "PrintBridge is running",
                data
        );

        ctx.json(response);

    }

}
