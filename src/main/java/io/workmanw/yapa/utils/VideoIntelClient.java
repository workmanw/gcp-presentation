package io.workmanw.yapa.utils;

import com.google.api.gax.rpc.OperationFuture;
import com.google.cloud.videointelligence.v1beta2.*;
import com.google.longrunning.Operation;
import io.workmanw.yapa.models.AnalysisVideoIntelModel;
import io.workmanw.yapa.models.AnalysisVideoIntelModel.VideoIntelItem;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class VideoIntelClient {
  private static final Logger log = Logger.getLogger(VisionClient.class.getName());

  public AnalysisVideoIntelModel analyzeVideo(String gcsUri) {
    List<VideoIntelItem> videoIntelItems = new ArrayList<>();

    try (VideoIntelligenceServiceClient client = VideoIntelligenceServiceClient.create()) {
      LabelDetectionConfig.Builder ldcBuilder = LabelDetectionConfig.newBuilder();
      ldcBuilder.setLabelDetectionModeValue(3);
      VideoContext.Builder vcBuilder = VideoContext.newBuilder();
      vcBuilder.setLabelDetectionConfig(ldcBuilder.build());

      // Provide path to file hosted on GCS as "gs://bucket-name/..."
      AnnotateVideoRequest request = AnnotateVideoRequest.newBuilder()
              .setInputUri(gcsUri)
              .addFeatures(Feature.LABEL_DETECTION)
              .setVideoContext(vcBuilder.build())
              .build();

      log.info(Integer.toString(request.getVideoContext().getLabelDetectionConfig().getLabelDetectionModeValue()));

      // Create an operation that will contain the response when the operation completes.
      OperationFuture<AnnotateVideoResponse, AnnotateVideoProgress, Operation> operation =
              client.annotateVideoAsync(request);

      for (VideoAnnotationResults results : operation.get().getAnnotationResultsList()) {
        for (LabelAnnotation labelAnnotation : results.getSegmentLabelAnnotationsList()) {
          videoIntelItems.add(this.buildVideoIntelModel(labelAnnotation, VideoIntelItem.TYPE_SEGEMENT_LABEL));
        }

        // process shot label annotations
        for (LabelAnnotation labelAnnotation : results.getShotLabelAnnotationsList()) {
          videoIntelItems.add(this.buildVideoIntelModel(labelAnnotation, VideoIntelItem.TYPE_SHOT_LABEL));
        }

        // process frame label annotations
        for (LabelAnnotation labelAnnotation : results.getFrameLabelAnnotationsList()) {
          videoIntelItems.add(this.buildVideoIntelModel(labelAnnotation, VideoIntelItem.TYPE_FRAME_LABEL));
        }
      }
    } catch (Exception e) {
      log.severe(e.toString());
    }

    return new AnalysisVideoIntelModel(videoIntelItems);
  }

  protected VideoIntelItem buildVideoIntelModel(LabelAnnotation labelAnnotation, String modelType) {
    VideoIntelItem videoIntelItem = new VideoIntelItem();
    videoIntelItem.setType(modelType);
    videoIntelItem.setMid(labelAnnotation.getEntity().getEntityId());
    videoIntelItem.setDescription(labelAnnotation.getEntity().getDescription());
    videoIntelItem.setType(modelType);
    videoIntelItem.setScore(this.scoreLabelAnnotation(labelAnnotation));
    return videoIntelItem;
  }

  protected float scoreLabelAnnotation(LabelAnnotation labelAnnotation) {
    float total = 0;
    float score = 0;
    int count = 0;

    if (labelAnnotation.getSegmentsCount() > 0) {
      for (LabelSegment segment : labelAnnotation.getSegmentsList()) {
        total += segment.getConfidence();
        count++;
      }
    } else if (labelAnnotation.getFramesCount() > 0) {
      for (LabelFrame frame : labelAnnotation.getFramesList()) {
        total += frame.getConfidence();
        count++;
      }
    }

    if (count > 0) {
      score = total / count;
    }

    return score;
  }
}