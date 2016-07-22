package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.MultipartProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

@Slf4j
@Service
public class LampInitService {

    @Autowired
    private MultipartProperties multipartProperties;

    @PostConstruct
    public void init() {
        String location = multipartProperties.getLocation();
        if (StringUtils.isNotBlank(location)) {
            File dir = new File(location);
            if (!dir.exists()) {
                log.info("File upload location not exists : {}", location);
                dir.mkdirs();
            }
        }
    }

}
