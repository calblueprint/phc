class Api::V1::EventRegistrationsController < ApplicationController
  skip_before_action :verify_authenticity_token
  before_action :verify_security_token

  def create
    sf_id = params.delete :account_sfid
    if sf_id.nil? || sf_id.empty? || sf_id == "\"\""
      # Create account if salesforce id was not passed in
      # TODO: Right now, the client basically creates accounts through this endpoint
      # We should seperate it into 2 API calls, the first to api/v1/create (which is unused right now)
      params[:sf_id] = nil
      account = Account.spawn(params)
    else
      # We retrieve the corresponding account if the salesforce id was passed in
      account = Account.find_by(sf_id: sf_id)

      # An account should always be found for a salesforce ID,
      # but if not create the account anyway and treat it as a new account
      if account.nil?
        account = Account.spawn(params)
      else
        account_params = params.permit(Account.fields)
        account.update account_params
      end
    end
    account.update(updated: false)
    reg = account.event_registrations.create(Number__c: params[:Number__c])
    Service.services.each do |name|
      service = reg.services.create(name: name)
      # TODO: Make sure we are receiving TRUE as a boolean and not as a string
      # TODO: Figure out when the heck each case happens and how to test each case
      if params[name] == true || params[name] == "true"
        service.applied!
      end

      # TODO: Don't save every service to speed up queries
      # if params.include? name
      #   service = reg.services.create(name: name)
      #   # TODO: Make sure we are receiving TRUE as a boolean and not as a string
      #   # TODO: Figure out when the heck each case happens and how to test each case
      #   if params[name] == true || params[name] == "true"
      #     service.applied!
      #   end
      # end
    end

    status = reg.save ? "Successfully saved event registration!" : "Failed to save event registration"
    api_message_response(200, status)
  end

  def search
    qr_code = request.headers["HTTP_NUMBER__C"]
    puts qr_code
    render json: { present: (EventRegistration.exists?(Number__c: qr_code) ? true : false) }
  end

  def get_applied
    event_registration = EventRegistration.find_by(Number__c: params[:Number__c])
    if event_registration
      applied_services = event_registration.services.where(status: Service.statuses[:applied]).map(&:name)
      render json: { status: "true", services: applied_services }
    else
      api_message_response(404, "Event registration with that number does not exist.")
    end
  end

  def update_service
    event_registration = EventRegistration.find_by(Number__c: params[:Number__c])
    if event_registration.nil?
      api_message_response(404, "Event registration with that number does not exist.")
      return
    end

    service = event_registration.services.find_by(name: params[:service_name])
    if service.nil?
      api_message_response(404, "Service with that name does not exist.")
      return
    end

    case service.status
    when "unspecified"
      service.drop_in!
      api_message_response(201, "Client's status set to drop-in.")
    when "applied"
      service.received!
      api_message_response(201, "Client's status set to received.")
    when "drop_in"
      api_message_response(201, "Client has already received service.")
    when "received"
      api_message_response(201, "Client has already received service.")
    else
      api_message_response(400, "Invalid status.")
    end
  end

  def update_feedback
    event_registration = EventRegistration.find_by(Number__c: params[:Number__c])
    if event_registration
      event_registration.update(event_feedback_params)
      api_message_response(201, "Successfully recieved feedback!")
    else
      api_message_response(404, "Event registration with that number does not exist.")
    end
  end

  private

  def event_feedback_params
    event_params = params.permit(:Number__c, :Experience__c, :Feedback__c, :Services_Needed__c => [])
    event_params[:Services_Needed__c].join(', ')
    event_params
  end

end
