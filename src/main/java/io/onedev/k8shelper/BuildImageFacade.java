package io.onedev.k8shelper;

import io.onedev.commons.utils.ExplicitException;
import io.onedev.commons.utils.PathUtils;
import io.onedev.commons.utils.TaskLogger;
import io.onedev.commons.utils.command.Commandline;
import io.onedev.commons.utils.command.LineConsumer;

import javax.annotation.Nullable;
import java.io.File;
import java.io.Serializable;

import static io.onedev.commons.utils.StringUtils.parseQuoteTokens;
import static io.onedev.k8shelper.KubernetesHelper.replacePlaceholders;

public class BuildImageFacade extends LeafFacade {

	private static final long serialVersionUID = 1L;

	private final String buildPath;
	
	private final String dockerfile;
	
	private final Output output;

	private final boolean removeDanglingImages;

	private final String builtInRegistryAccessToken;

	private final String moreOptions;

	public BuildImageFacade(@Nullable String buildPath, @Nullable String dockerFile,
							Output output, boolean removeDanglingImages,
							@Nullable String builtInRegistryAccessToken,
							@Nullable String moreOptions) {
		this.buildPath = buildPath;
		this.dockerfile = dockerFile;
		this.output = output;
		this.removeDanglingImages = removeDanglingImages;
		this.builtInRegistryAccessToken = builtInRegistryAccessToken;
		this.moreOptions = moreOptions;
	}

	@Nullable
	public String getBuildPath() {
		return buildPath;
	}

	@Nullable
	public String getDockerfile() {
		return dockerfile;
	}

	public Output getOutput() {
		return output;
	}

	public boolean isRemoveDanglingImages() {
		return removeDanglingImages;
	}

	@Nullable
	public String getBuiltInRegistryAccessToken() {
		return builtInRegistryAccessToken;
	}

	@Nullable
	public String getMoreOptions() {
		return moreOptions;
	}

	public interface Output extends Serializable {

		void execute(Commandline docker, File hostBuildHome, LineConsumer infoLogger, LineConsumer errorLogger);

	}

	public static class RegistryOutput implements Output {

		private static final long serialVersionUID = 1L;

		private final String tags;

		public RegistryOutput(String tags) {
			this.tags = tags;
		}

		@Override
		public void execute(Commandline docker, File hostBuildHome, LineConsumer infoLogger, LineConsumer errorLogger) {
			docker.addArgs("--push");
			String[] parsedTags = parseQuoteTokens(replacePlaceholders(tags, hostBuildHome));
			for (String tag : parsedTags)
				docker.addArgs("-t", tag);
			docker.execute(infoLogger, errorLogger).checkReturnCode();
		}
	}

	public static class OCIOutput implements Output {

		private static final long serialVersionUID = 1L;

		private final String destPath;

		public OCIOutput(String destPath) {
			this.destPath = destPath;
		}

		@Override
		public void execute(Commandline docker, File hostBuildHome, LineConsumer infoLogger, LineConsumer errorLogger) {
			if (!PathUtils.isSubPath(destPath))
				throw new ExplicitException("OCI output path should be a relative path not containing '..'");
			docker.addArgs("-o type=oci,dest=-");

		}
	}

}
