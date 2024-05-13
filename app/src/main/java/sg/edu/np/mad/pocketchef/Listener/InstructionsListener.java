package sg.edu.np.mad.pocketchef.Listener;

import java.util.List;

import sg.edu.np.mad.pocketchef.Models.InstructionsResponse;

public interface InstructionsListener {
    void didFetch(List<InstructionsResponse> response, String message);
    void didError(String message);
}
