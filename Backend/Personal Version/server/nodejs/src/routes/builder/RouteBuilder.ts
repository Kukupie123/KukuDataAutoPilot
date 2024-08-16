import { Router } from "express";
import { IKDAPRoute, RouteDefinition } from "../interface/IKDAPRoute";

export async function buildRoute<T extends IKDAPRoute>(router: Router, routerClass: { new(...args: any[]): T }): Promise<void> {
    const r = new routerClass();
    await r.initRouter()

    const routes: RouteDefinition[] = r.getRoutes();

    routes.forEach((route) => {
        switch (route.method) {
            case "get":
                router.get(route.path, route.handler);
                break;
            case "post":
                router.post(route.path, route.handler);
                break;
            case "put":
                router.put(route.path, route.handler);
                break;
            case "delete":
                router.delete(route.path, route.handler);
                break;
            case "patch":
                router.patch(route.path, route.handler);
                break;
            case "options":
                router.options(route.path, route.handler);
                break;
            case "head":
                router.head(route.path, route.handler);
                break;
            default:
                throw new Error(`Unsupported method: ${route.method}`);
        }
    });
}
