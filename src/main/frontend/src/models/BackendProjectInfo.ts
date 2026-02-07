export interface BackendProjectInfo {
    sanjyClientWeb: Project;
    sanjyServer: Project;
}

export interface Project {
    version: Version;
    runtimeMode: string;
}

export interface Version {
    current: string;
    latest: string;
    isLatest: boolean;
}